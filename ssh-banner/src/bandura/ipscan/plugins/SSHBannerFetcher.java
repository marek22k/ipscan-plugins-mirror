package bandura.ipscan.plugins;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

public class SSHBannerFetcher extends PortTextFetcher {

    public SSHBannerFetcher(ScannerConfig scannerConfig) {
        super(scannerConfig, 22, "", "^(.*)$");
    }

    @CheckReturnValue
    @NonNull
    public String getId() {
        return "fetcher.sshBanner";
    }

}
