package dev.anhcraft.advancedkeep.api;

public class ApiProvider {
    private static KeepAPI api;

    /**
     * Gets the AdvancedKeep API.
     * <br>
     * This method may throw {@link UnsupportedOperationException} if the API is not ready.
     * @return {@link KeepAPI}
     */
    public static KeepAPI consume() {
        if(api == null)
            throw new UnsupportedOperationException("API is not ready");
        return api;
    }
}
