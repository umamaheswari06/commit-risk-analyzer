package com.commitrisk.analyzer;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Case-insensitive keyword sets used to detect which risk category a
 * changed file or its content belongs to. Kept as a single source of
 * truth so the detection rules are easy to audit and extend.
 */
public final class PatternLibrary {

    private PatternLibrary() {}

    public static final List<String> SENSITIVE_FILENAME_KEYWORDS = List.of(
            "security", "auth", "authentication", "authorization", "payment",
            "transaction", "userrepository", "usermanagement", "user",
            "databaseconfig", "database", "config", "gateway", "apigateway",
            "credential", "token", "session", "role", "permission"
    );

    public static final List<String> DATABASE_KEYWORDS = List.of(
            "select ", "insert ", "update ", "delete ", "alter table",
            "create table", "drop table", "repository", "dao", "jpa",
            "hibernate", "@query", "@entity", "@table", "@transactional",
            "sql", "resultset", "preparedstatement"
    );

    public static final List<String> SECURITY_KEYWORDS = List.of(
            "jwt", "authentication", "authorization", "password", "role",
            "permission", "security", "token", "oauth", "encrypt", "decrypt",
            "hash", "credential", "bcrypt", "csrf", "xss", "sanitize"
    );

    public static final List<String> PAYMENT_KEYWORDS = List.of(
            "payment", "invoice", "billing", "checkout", "refund", "charge",
            "transaction", "stripe", "paypal", "razorpay", "price", "tax",
            "currency", "wallet"
    );

    public static final List<String> EXCEPTION_KEYWORDS = List.of(
            "try", "catch", "throw", "throws", "exception", "finally"
    );

    public static final List<String> API_KEYWORDS = List.of(
            "controller", "@restcontroller", "@getmapping", "@postmapping",
            "@putmapping", "@deletemapping", "@requestmapping", "endpoint",
            "@pathvariable", "@requestbody", "restapi", " api "
    );

    public static final List<String> CONFIGURATION_FILENAMES = List.of(
            "pom.xml", "build.gradle", "application.properties",
            "application.yml", "application.yaml", "package.json",
            "dockerfile", "docker-compose", ".env", "web.xml"
    );

    /** Matches a whole word regardless of surrounding punctuation/case. */
    public static boolean containsKeyword(String haystackLowerCase, String keyword) {
        if (keyword.contains(" ") || keyword.startsWith("@") || keyword.startsWith(".")) {
            return haystackLowerCase.contains(keyword);
        }
        Pattern p = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
        return p.matcher(haystackLowerCase).find();
    }
}
