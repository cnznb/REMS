public class old{
    public Set<LanguageSourceSet> getAllSources() {
        Set<LanguageSourceSet> sources = Sets.newLinkedHashSet(super.getAllSources());
        sources.addAll(testedBinary.getAllSources());
        return sources;
    }
}
