public class old{
    public void setUp() {

      this.mvc = MockMvcBuilders.webAppContextSetup(context).//
          defaultRequest(get("/").accept(TestMvcClient.DEFAULT_MEDIA_TYPE)).build();
      this.client = new TestMvcClient(mvc, discoverers);
    }
}
