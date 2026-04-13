package com.smartcrop.service;

import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;

/**
 * Single shared Supabase client for the entire application.
 *
 * Usage (anywhere in the app):
 * SupabaseService db = SupabaseService.getInstance();
 *
 * Covers:
 * 1. Auth - signIn, signUp, signOut, isSignedIn, getSignedInEmail
 * 2. Generic - getRows, insertRow, updateRows, deleteRows
 * 3. Soil - saveSoilData, loadLatestSoilData, loadAllSoilData
 * 4. Crops - loadCrops
 * 5. Reports - loadReports, saveReport, deleteReport
 */
public class SupabaseService {

    // ── Credentials ───────────────────────────────────────────────────────────
    private static final String BASE_URL = "https://ztilvvkixxkvjextlvuv.supabase.co";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp0aWx2dmtpeHhrdmpleHRsdnV2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU3MDk1NTQsImV4cCI6MjA5MTI4NTU1NH0.kbtD80LqVZH_1pNTs5NbFwKhv1UJzqSNYvkpTUS9iVs";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static final SupabaseService INSTANCE = new SupabaseService();

    public static SupabaseService getInstance() {
        return INSTANCE;
    }

    // ── Session state ─────────────────────────────────────────────────────────
    private final OkHttpClient http = new OkHttpClient();
    private final Gson gson = new Gson();
    private String accessToken = null;
    private String signedInEmail = null;

    private SupabaseService() {
    }

    // =========================================================================
    // 1. AUTH
    // =========================================================================

    /**
     * Sign in with email + password.
     * Stores JWT internally — all subsequent calls use it automatically.
     * Returns the confirmed email on success, throws on failure.
     */
    public String signIn(String email, String password) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("email", email);
        payload.addProperty("password", password);

        Request req = new Request.Builder()
                .url(BASE_URL + "/auth/v1/token?grant_type=password")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(toBody(payload))
                .build();

        try (Response res = http.newCall(req).execute()) {
            String rb = readBody(res);
            if (!res.isSuccessful())
                throw new IOException(parseError(rb, "Login failed", res.code()));
            storeSession(JsonParser.parseString(rb).getAsJsonObject(), email);
            return signedInEmail;
        }
    }

    /**
     * Register a new user. Auto-signs them in on success.
     * Returns the email on success, throws on failure.
     */
    public String signUp(String email, String password) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("email", email);
        payload.addProperty("password", password);

        Request req = new Request.Builder()
                .url(BASE_URL + "/auth/v1/signup")
                .addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(toBody(payload))
                .build();

        try (Response res = http.newCall(req).execute()) {
            String rb = readBody(res);
            if (!res.isSuccessful())
                throw new IOException(parseError(rb, "Sign up failed", res.code()));
            storeSession(JsonParser.parseString(rb).getAsJsonObject(), email);
            return signedInEmail;
        }
    }

    /** Clear the stored session. Call on logout. */
    public void signOut() {
        accessToken = null;
        signedInEmail = null;
    }

    public boolean isSignedIn() {
        return accessToken != null;
    }

    public String getSignedInEmail() {
        return signedInEmail;
    }

    // =========================================================================
    // 2. GENERIC REST
    // =========================================================================

    /**
     * SELECT * FROM table.
     *
     * @param table  e.g. "soil_data"
     * @param filter PostgREST filter string: "column=op.value"
     *               e.g. "user_email=eq.foo@bar.com", "id=eq.5", null = no filter
     * @return raw JSON array string
     */
    public String getRows(String table, String filter) throws IOException {
        HttpUrl.Builder ub = HttpUrl.parse(BASE_URL + "/rest/v1/" + table)
                .newBuilder()
                .addQueryParameter("select", "*");
        addFilter(ub, filter);

        try (Response res = http.newCall(authed(new Request.Builder().url(ub.build()).get()).build()).execute()) {
            String body = readBody(res);
            if (!res.isSuccessful())
                throw new IOException("GET " + table + " failed " + res.code() + ": " + body);
            return body;
        }
    }

    /** SELECT * with no filter */
    public String getRows(String table) throws IOException {
        return getRows(table, null);
    }

    /**
     * SELECT with filter + ordering + limit.
     *
     * @param orderCol  column name to order by, or null for no ordering
     * @param ascending true = ASC, false = DESC
     * @param limit     max rows to return; 0 = no limit
     */
    public String getRows(String table, String filter,
            String orderCol, boolean ascending, int limit) throws IOException {
        HttpUrl.Builder ub = HttpUrl.parse(BASE_URL + "/rest/v1/" + table)
                .newBuilder()
                .addQueryParameter("select", "*");
        addFilter(ub, filter);
        if (orderCol != null && !orderCol.isBlank())
            ub.addQueryParameter("order", orderCol + "." + (ascending ? "asc" : "desc"));
        if (limit > 0)
            ub.addQueryParameter("limit", String.valueOf(limit));

        try (Response res = http.newCall(authed(new Request.Builder().url(ub.build()).get()).build()).execute()) {
            String body = readBody(res);
            if (!res.isSuccessful())
                throw new IOException("GET " + table + " failed " + res.code() + ": " + body);
            return body;
        }
    }

    /**
     * INSERT a single row.
     *
     * @param table   target table name
     * @param jsonRow JSON object string, e.g. {"name":"Rice","suitability":"good"}
     * @return inserted row(s) as JSON array string
     */
    public String insertRow(String table, String jsonRow) throws IOException {
        Request req = authed(new Request.Builder()
                .url(BASE_URL + "/rest/v1/" + table)
                .addHeader("Prefer", "return=representation")
                .post(toBody(jsonRow)))
                .build();
        try (Response res = http.newCall(req).execute()) {
            String rb = readBody(res);
            if (!res.isSuccessful())
                throw new IOException("INSERT " + table + " failed " + res.code() + ": " + rb);
            return rb;
        }
    }

    /**
     * UPDATE rows matching filter.
     *
     * @param table     target table
     * @param patchJson JSON object with fields to change, e.g. {"title":"new"}
     * @param filter    e.g. "id=eq.5"
     * @return updated row(s) as JSON array string
     */
    public String updateRows(String table, String patchJson, String filter) throws IOException {
        HttpUrl.Builder ub = HttpUrl.parse(BASE_URL + "/rest/v1/" + table).newBuilder();
        addFilter(ub, filter);

        Request req = authed(new Request.Builder()
                .url(ub.build())
                .addHeader("Prefer", "return=representation")
                .patch(toBody(patchJson)))
                .build();
        try (Response res = http.newCall(req).execute()) {
            String rb = readBody(res);
            if (!res.isSuccessful())
                throw new IOException("UPDATE " + table + " failed " + res.code() + ": " + rb);
            return rb;
        }
    }

    /**
     * DELETE rows matching filter.
     *
     * @param table  target table
     * @param filter e.g. "id=eq.5" or "user_email=eq.foo@bar.com"
     */
    public void deleteRows(String table, String filter) throws IOException {
        HttpUrl.Builder ub = HttpUrl.parse(BASE_URL + "/rest/v1/" + table).newBuilder();
        addFilter(ub, filter);

        Request req = authed(new Request.Builder().url(ub.build()).delete()).build();
        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                String rb = readBody(res);
                throw new IOException("DELETE from " + table + " failed " + res.code() + ": " + rb);
            }
        }
    }

    // =========================================================================
    // 3. SOIL DATA
    // =========================================================================

    /** Insert a new soil reading for a user */
    public String saveSoilData(String userEmail, String soilType,
            double ph, double nitrogen, double phosphorus,
            double potassium, double organicMatter, double moisture)
            throws IOException {

        JsonObject obj = new JsonObject();
        obj.addProperty("user_email", userEmail);
        obj.addProperty("soil_type", soilType);
        obj.addProperty("ph", ph);
        obj.addProperty("nitrogen", nitrogen);
        obj.addProperty("phosphorus", phosphorus);
        obj.addProperty("potassium", potassium);
        obj.addProperty("organic_matter", organicMatter);
        obj.addProperty("moisture", moisture);

        // 🔥 important: ensure ordering works

        return insertRow("soil_data", obj.toString());
    }

    public String loadLatestSoilData(String userEmail) throws IOException {
        return getRows(
                "soil_data",
                "user_email=eq." + userEmail,
                "created_at",
                false,
                1);
    }

    /** Most recent soil entry for a user (1 row) */
    public JsonObject getLatestSoilDataParsed(String userEmail) throws IOException {

        // 🔥 Fetch latest data
        String json = getRows(
                "soil_data",
                "user_email=eq." + userEmail,
                "created_at",
                false,
                1);

        System.out.println("📦 Soil fetch response: " + json);

        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

        // ❌ No data case
        if (arr.size() == 0) {
            System.out.println("❌ No soil data found for: " + userEmail);
            return null;
        }

        JsonObject latest = arr.get(0).getAsJsonObject();

        // 🔥 Safety check (important)
        if (!latest.has("ph") || latest.get("ph").isJsonNull()) {
            System.out.println("⚠️ Invalid soil data structure");
            return null;
        }

        System.out.println("✅ Latest soil loaded successfully");
        return latest;
    }

    // =========================================================================
    // 4. CROPS
    // =========================================================================

    /**
     * Load all crops from the crops master table.
     * This is shared data — same for every user, read-only from app.
     */
    public String loadCrops() throws IOException {
        return getRows("crops", null, "id", true, 0);
    }

    // =========================================================================
    // 5. REPORTS
    // =========================================================================

    /** All reports for a user, newest first */
    public String loadReports(String userEmail) throws IOException {
        return getRows("reports", "user_email=eq." + userEmail, "created_at", false, 0);
    }

    /** Save a new report entry */
    public String saveReport(String userEmail, String title, String type, String content)
            throws IOException {

        JsonObject obj = new JsonObject();
        obj.addProperty("user_email", userEmail);
        obj.addProperty("title", title);
        obj.addProperty("type", type);
        obj.addProperty("content", content);

        return insertRow("reports", gson.toJson(obj));
    }

    /** Delete a specific report by its numeric id */
    public void deleteReport(long reportId) throws IOException {
        deleteRows("reports", "id=eq." + reportId);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /** Attach API key + Bearer token to every outgoing request */
    private Request.Builder authed(Request.Builder b) {
        String bearer = (accessToken != null) ? accessToken : ANON_KEY;
        return b.addHeader("apikey", ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + bearer);
    }

    /** Parse "column=op.value" and add as query param */
    private void addFilter(HttpUrl.Builder ub, String filter) {
        if (filter == null || filter.isBlank())
            return;
        String[] parts = filter.split("=", 2);
        if (parts.length == 2)
            ub.addQueryParameter(parts[0], parts[1]);
    }

    private RequestBody toBody(JsonObject obj) {
        return RequestBody.create(gson.toJson(obj), MediaType.get("application/json; charset=utf-8"));
    }

    private RequestBody toBody(String raw) {
        return RequestBody.create(raw, MediaType.get("application/json; charset=utf-8"));
    }

    private String readBody(Response res) throws IOException {
        return res.body() != null ? res.body().string() : "";
    }

    private void storeSession(JsonObject json, String fallbackEmail) {
        if (json.has("access_token"))
            accessToken = json.get("access_token").getAsString();
        if (json.has("user") && json.getAsJsonObject("user").has("email"))
            signedInEmail = json.getAsJsonObject("user").get("email").getAsString();
        else
            signedInEmail = fallbackEmail;
    }

    private String parseError(String body, String fallback, int code) {
        try {
            JsonObject err = JsonParser.parseString(body).getAsJsonObject();
            for (String key : new String[] { "error_description", "msg", "message", "error" }) {
                if (err.has(key) && !err.get(key).isJsonNull())
                    return err.get(key).getAsString();
            }
        } catch (Exception ignored) {
        }
        return fallback + " (HTTP " + code + ")";
    }
}
