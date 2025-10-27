import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.fetchers.PortTextFetcher;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SSHBannerFetcher extends PortTextFetcher {
    private static ResourceBundle messages;

   	static {
		Locale locale = Config.getConfig().getLocale();
        try {
            messages = ResourceBundle.getBundle("plugin_messages", locale, 
                SSHBannerFetcher.class.getClassLoader());
        } catch (MissingResourceException e) {
            messages = ResourceBundle.getBundle("plugin_messages", Locale.ENGLISH,
                SSHBannerFetcher.class.getClassLoader());
        }
    }

	public SSHBannerFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig, 22, "", "^(.*)$");
	}

	public String getId() {
		return "fetcher.sshBanner";
	}

	public String getName() {
		return messages.getString("fetcher.sshBanner");
	}

}