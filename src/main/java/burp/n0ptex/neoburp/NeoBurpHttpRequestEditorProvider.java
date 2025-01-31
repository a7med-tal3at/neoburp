package burp.n0ptex.neoburp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.ui.editor.extension.HttpRequestEditorProvider;

public class NeoBurpHttpRequestEditorProvider implements HttpRequestEditorProvider {

    private final MontoyaApi api;

    public NeoBurpHttpRequestEditorProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext) {

        return new NeoBurpHttpRequestEditor(this.api);
    }
}
