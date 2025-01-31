package burp.n0ptex.neoburp.AutoCompletion;

import java.util.List;

public class AutoCompletionWords {

    private List<String> words;

    public List<String> getWords() {

        words = List.of(
                "CONNECT",
                "DELETE",
                "GET",
                "HEAD",
                "OPTIONS",
                "PATCH",
                "POST",
                "PUT",
                "TRACE",
                "Accept: ", "Accept-CH: ", "Accept-Encoding: ", "Accept-Language: ", "Accept-Patch: ", "Accept-Post: ",
                "Accept-Ranges: ", "Access-Control-Allow-Credentials: ", "Access-Control-Allow-Headers: ",
                "Access-Control-Allow-Methods: ", "Access-Control-Allow-Origin: ", "Access-Control-Expose-Headers: ",
                "Access-Control-Max-Age: ", "Access-Control-Request-Headers: ", "Access-Control-Request-Method: ",
                "Age: ", "Allow: ", "Alt-Svc: ", "Alt-Used: ", "Attribution-Reporting-Eligible: ",
                "Attribution-Reporting-Register-Source: ", "Attribution-Reporting-Register-Trigger: ",
                "Authorization: ", "Cache-Control: ", "Clear-Site-Data: ", "Connection: ", "Content-Digest: ",
                "Content-Disposition: ", "Content-DPR: ", "Content-Encoding: ", "Content-Language: ",
                "Content-Length: ", "Content-Location: ", "Content-Range: ", "Content-Security-Policy: ",
                "Content-Security-Policy-Report-Only: ", "Content-Type: ", "Cookie: ", "Critical-CH: ",
                "Cross-Origin-Embedder-Policy: ", "Cross-Origin-Opener-Policy: ", "Cross-Origin-Resource-Policy: ",
                "Date: ", "Device-Memory: ", "DNT: ", "Downlink: ", "DPR: ", "Early-Data: ", "ECT: ", "ETag: ",
                "Expect: ", "Expect-CT: ", "Expires: ", "Forwarded: ", "From: ", "Host: ", "If-Match: ",
                "If-Modified-Since: ", "If-None-Match: ", "If-Range: ", "If-Unmodified-Since: ", "Keep-Alive: ",
                "Last-Modified: ", "Link: ", "Location: ", "Max-Forwards: ", "NEL: ", "No-Vary-Search: ",
                "Observe-Browsing-Topics: ", "Origin: ", "Origin-Agent-Cluster: ", "Permissions-Policy: ", "Pragma: ",
                "Priority: ", "Proxy-Authenticate: ", "Proxy-Authorization: ", "Range: ", "Referer: ",
                "Referrer-Policy: ", "Refresh: ", "Report-To: ", "Reporting-Endpoints: ", "Repr-Digest: ",
                "Retry-After: ", "RTT: ", "Save-Data: ", "Sec-Browsing-Topics: ", "Sec-CH-Prefers-Color-Scheme: ",
                "Sec-CH-Prefers-Reduced-Motion: ", "Sec-CH-Prefers-Reduced-Transparency: ", "Sec-CH-UA: ",
                "Sec-CH-UA-Arch: ", "Sec-CH-UA-Bitness: ", "Sec-CH-UA-Full-Version: ", "Sec-CH-UA-Full-Version-List: ",
                "Sec-CH-UA-Mobile: ", "Sec-CH-UA-Model: ", "Sec-CH-UA-Platform: ", "Sec-CH-UA-Platform-Version: ",
                "Sec-Fetch-Dest: ", "Sec-Fetch-Mode: ", "Sec-Fetch-Site: ", "Sec-Fetch-User: ", "Sec-GPC: ",
                "Sec-Purpose: ", "Sec-WebSocket-Accept: ", "Sec-WebSocket-Extensions: ", "Sec-WebSocket-Key: ",
                "Sec-WebSocket-Protocol: ", "Sec-WebSocket-Version: ", "Server: ", "Server-Timing: ",
                "Service-Worker: ", "Service-Worker-Allowed: ", "Service-Worker-Navigation-Preload: ", "Set-Cookie: ",
                "Set-Login: ", "SourceMap: ", "Speculation-Rules: ", "Strict-Transport-Security: ",
                "Supports-Loading-Mode: ", "TE: ", "Timing-Allow-Origin: ", "Tk: ", "Trailer: ", "Transfer-Encoding: ",
                "Upgrade: ", "Upgrade-Insecure-Requests: ", "User-Agent: ", "Vary: ", "Via: ", "Viewport-Width: ",
                "Want-Content-Digest: ", "Want-Repr-Digest: ", "Warning: ", "Width: ", "WWW-Authenticate: ",
                "X-Content-Type-Options: ", "X-DNS-Prefetch-Control: ", "X-Forwarded-For: ", "X-Forwarded-Host: ",
                "X-Forwarded-Proto: ", "X-Frame-Options: ", "X-Permitted-Cross-Domain-Policies: ", "X-Powered-By: ",
                "X-Robots-Tag: ", "X-XSS-Protection: ",
                // Payloads: A simple workaround that will be changed in the next release
                "%xss1:<ScRipt>alert('n0pTeX')</sCrIPT>",
                "%xss2:<img src=x onerror=\"alert('n0pTeX')\">",
                "%sql1:' OR '1'='1",
                "%xxe1:<?xml version=\"1.0\"?>\n<!DOCTYPE foo [  \n<!ELEMENT foo (#ANY)>\n<!ENTITY xxe SYSTEM \"file:///etc/passwd\"> ]><foo>&xxe;</foo>",
                "%ssti1:${{<%[%'\"}}%\\."

        );

        return words;
    }
}
