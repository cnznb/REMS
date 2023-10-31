public class old{
    private static void fillRepresentations(Swagger swagger, Contract contract) {
        if (swagger.getDefinitions() == null) {
            return;
        }

        for (String key : swagger.getDefinitions().keySet()) {
            Model model = swagger.getDefinitions().get(key);
            Representation representation = new Representation();
            representation.setDescription(model.getDescription());
            representation.setName(key);
            representation.setRaw(false);
            // TODO: example not implemented in RWADef (built from properties examples)
            fillProperties(model, representation);
            contract.getRepresentations().add(representation);
        }
    }
}
