import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Config;

public class SSHBannerFetcher extends PortTextFetcher {

	public SSHBannerFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig, 22, "", "^(.*)$");
	}

	public String getId() {
		return "fetcher.sshBanner";
	}

}
