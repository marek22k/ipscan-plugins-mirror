package bandura.ipscan.plugins;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;

public class SSHBannerFetcher extends PortTextFetcher {

    public SSHBannerFetcher(ScannerConfig scannerConfig) {
        super(scannerConfig, 22, "", "^(.*)$");
    }

    public String getId() {
        return "fetcher.sshBanner";
    }

}
