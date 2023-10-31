public class old{
    public String getIconImageURL(int size) {
        String blavatarUrl = GravatarUtils.blavatarFromUrl(this.getUrl(), size);
        if (iconURL == null) {
            return blavatarUrl;
        }
        if (isPhotonURL(iconURL)) {
            return UrlUtils.removeQuery(iconURL).concat(String.format("w=%d&h=%d", size, size));
        }
        if (isBlavatarURL(iconURL)) {
            return UrlUtils.removeQuery(iconURL).concat(String.format("s=%d", size));
        }
        return blavatarUrl;
    }
}
