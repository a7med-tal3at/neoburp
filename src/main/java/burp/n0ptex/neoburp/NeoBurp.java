package burp.n0ptex.neoburp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class NeoBurp implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("NeoBurp");
        api.userInterface().registerHttpRequestEditorProvider(new NeoBurpHttpRequestEditorProvider(api));
    }
}
