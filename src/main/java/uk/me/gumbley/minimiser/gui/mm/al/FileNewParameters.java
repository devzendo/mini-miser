package uk.me.gumbley.minimiser.gui.mm.al;

public class FileNewParameters {
    private final String newPath;
    private final boolean newEncrypted;
    private final String newPassword;
    private final String newCurrency;

    public FileNewParameters(final String path, final boolean encrypted, final String password, final String currency) {
        this.newPath = path;
        this.newEncrypted = encrypted;
        this.newPassword = password;
        this.newCurrency = currency;
    }

    public String getCurrency() {
        return newCurrency;
    }

    public String getPassword() {
        return newPassword;
    }

    public String getPath() {
        return newPath;
    }
    
    public boolean isEncrypted() {
        return newEncrypted;
    }
}
