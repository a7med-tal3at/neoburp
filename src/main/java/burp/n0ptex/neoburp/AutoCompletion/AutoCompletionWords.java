package burp.n0ptex.neoburp.AutoCompletion;

import java.util.*;
import java.util.stream.Collectors;

import burp.n0ptex.neoburp.helpers.Enums.AutoCompletionType;

public class AutoCompletionWords {

    private static final Map<String, List<String>> HEADERS_MAP = new HashMap<>();

    static {
        HEADERS_MAP.put("Cache-Control", Arrays.asList(
                "public",
                "private",
                "no-cache",
                "no-store",
                "no-transform",
                "must-revalidate",
                "proxy-revalidate",
                "max-age=",
                "s-maxage=",
                "max-stale=",
                "min-fresh=",
                "stale-while-revalidate=",
                "stale-if-error=",
                "immutable",
                "only-if-cached"));

        HEADERS_MAP.put("Connection", Arrays.asList(
                "close",
                "keep-alive",
                "upgrade"));

        HEADERS_MAP.put("Content-Disposition", Arrays.asList(
                "attachment",
                "inline",
                "form-data"));

        HEADERS_MAP.put("Content-Encoding", Arrays.asList(
                "gzip",
                "deflate",
                "br",
                "compress",
                "identity"));

        HEADERS_MAP.put("Content-Type", Arrays.asList(
                "application/EDI-X12",
                "application/EDIFACT",
                "application/javascript",
                "application/octet-stream",
                "application/ogg",
                "application/pdf",
                "application/xhtml+xml",
                "application/x-shockwave-flash",
                "application/json",
                "application/ld+json",
                "application/xml",
                "application/zip",
                "application/x-www-form-urlencoded",
                "audio/mpeg",
                "audio/x-ms-wma",
                "audio/vnd.rn-realaudio",
                "audio/x-wav",
                "image/gif",
                "image/jpeg",
                "image/png",
                "image/tiff",
                "image/vnd.microsoft.icon",
                "image/x-icon",
                "image/vnd.djvu",
                "image/svg+xml",
                "multipart/mixed",
                "multipart/alternative",
                "multipart/form-data",
                "text/css",
                "text/csv",
                "text/html",
                "text/javascript",
                "text/plain",
                "text/xml"));

        HEADERS_MAP.put("DNT", Arrays.asList("1", "0"));
        HEADERS_MAP.put("Forwarded", Collections.singletonList(""));
        HEADERS_MAP.put("Host", Collections.singletonList(""));
        HEADERS_MAP.put("Origin", Collections.singletonList(""));
        HEADERS_MAP.put("Pragma", Collections.singletonList("no-cache"));
        HEADERS_MAP.put("Priority", Collections.singletonList(""));

        HEADERS_MAP.put("Transfer-Encoding", Arrays.asList(
                "chunked",
                "compress",
                "deflate",
                "gzip",
                "identity",
                "trailers"));

        HEADERS_MAP.put("X-Forwarded-For", Collections.singletonList(""));
        HEADERS_MAP.put("X-Forwarded-Host", Collections.singletonList(""));
        HEADERS_MAP.put("X-Forwarded-Proto", Collections.singletonList(""));
    }

    public List<String> getWords(AutoCompletionType type, String currentLine) {
        // If the current line ends with a space, trigger header value suggestions
        if (currentLine != null && currentLine.contains(": ")) {
            return getHeaderValues(currentLine);
        }

        switch (type) {
            case METHODS:
                return List.of(
                        "CONNECT",
                        "DELETE",
                        "GET",
                        "HEAD",
                        "OPTIONS",
                        "PATCH",
                        "POST",
                        "PUT",
                        "TRACE");
            case HEADERS:
                if (currentLine != null && currentLine.contains(":")) {
                    return getHeaderValues(currentLine);
                }
                return HEADERS_MAP.keySet().stream()
                        .map(header -> header + ":")
                        .collect(Collectors.toList());

            default:
                return Collections.emptyList();
        }
    }

    public List<String> getHeaderValues(String currentLine) {
        if (currentLine == null || !currentLine.contains(":")) {
            return Collections.emptyList();
        }

        String headerName = currentLine.substring(0, currentLine.indexOf(":")).trim();
        return HEADERS_MAP.getOrDefault(headerName, Collections.emptyList());
    }
}
