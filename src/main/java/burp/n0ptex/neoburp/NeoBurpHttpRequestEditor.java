package burp.n0ptex.neoburp;

import java.awt.Component;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.n0ptex.neoburp.components.Editor;

public class NeoBurpHttpRequestEditor implements ExtensionProvidedHttpRequestEditor {

    private final Editor editor;

    public NeoBurpHttpRequestEditor(MontoyaApi api) {
        editor = new Editor(api);
    }

    @Override
    public HttpRequest getRequest() {
        return HttpRequest.httpRequest(this.editor.getTextAreaContent());
    }

    @Override
    public void setRequestResponse(HttpRequestResponse requestResponse) {
        this.editor.setTextAreaContent(requestResponse.request().toString());
    }

    @Override
    public boolean isEnabledFor(HttpRequestResponse requestResponse) {
        return true;
    }

    @Override
    public String caption() {
        return "NeoBurp";
    }

    @Override
    public Component uiComponent() {
        return this.editor.ui();
    }

    @Override
    public Selection selectedData() {
        throw new UnsupportedOperationException("selectedData() Not supported yet.");
    }

    @Override
    public boolean isModified() {
        return true;
    }
}
