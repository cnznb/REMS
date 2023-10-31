public class old{
    public void testBasicMapping() throws Exception {
        final DBCollection hotels = getDb().getCollection("hotels");
        final DBCollection agencies = getDb().getCollection("agencies");

        getMorphia().map(Hotel.class);
        getMorphia().map(TravelAgency.class);
        final Hotel borg = new Hotel();
        borg.setName("Hotel Borg");
        borg.setStars(4);
        borg.setTakesCreditCards(true);
        borg.setStartDate(new Date());
        borg.setType(Hotel.Type.LEISURE);
        borg.getTags().add("Swimming pool");
        borg.getTags().add("Room service");
        borg.setTemp("A temporary transient value");
        borg.getPhoneNumbers().add(new PhoneNumber(354, 5152233, PhoneNumber.Type.PHONE));
        borg.getPhoneNumbers().add(new PhoneNumber(354, 5152244, PhoneNumber.Type.FAX));
        final Address address = new Address();
        address.setStreet("Posthusstraeti 11");
        address.setPostCode("101");
        borg.setAddress(address);
        BasicDBObject hotelDbObj = (BasicDBObject) getMorphia().toDBObject(borg);
        assertTrue(!(((DBObject) ((List) hotelDbObj.get("phoneNumbers")).get(0)).containsField(Mapper.CLASS_NAME_FIELDNAME)));
        hotels.save(hotelDbObj);
        Hotel borgLoaded = getMorphia().fromDBObject(Hotel.class, hotelDbObj, new DefaultEntityCache());
        assertEquals(borg.getName(), borgLoaded.getName());
        assertEquals(borg.getStars(), borgLoaded.getStars());
        assertEquals(borg.getStartDate(), borgLoaded.getStartDate());
        assertEquals(borg.getType(), borgLoaded.getType());
        assertEquals(borg.getAddress().getStreet(), borgLoaded.getAddress().getStreet());
        assertEquals(borg.getTags().size(), borgLoaded.getTags().size());
        assertEquals(borg.getTags(), borgLoaded.getTags());
        assertEquals(borg.getPhoneNumbers().size(), borgLoaded.getPhoneNumbers().size());
        assertEquals(borg.getPhoneNumbers().get(1), borgLoaded.getPhoneNumbers().get(1));
        assertNull(borgLoaded.getTemp());
        assertTrue(borgLoaded.getPhoneNumbers() instanceof Vector);
        assertNotNull(borgLoaded.getId());
        final TravelAgency agency = new TravelAgency();
        agency.setName("Lastminute.com");
        agency.getHotels().add(borgLoaded);
        final BasicDBObject agencyDbObj = (BasicDBObject) getMorphia().toDBObject(agency);
        agencies.save(agencyDbObj);
        final TravelAgency agencyLoaded = getMorphia().fromDBObject(TravelAgency.class,
                                                                    agencies.findOne(new BasicDBObject(Mapper.ID_KEY,
                                                                                                       agencyDbObj.get(Mapper.ID_KEY))),
                                                                    new DefaultEntityCache());
        assertEquals(agency.getName(), agencyLoaded.getName());
        assertEquals(1, agency.getHotels().size());
        assertEquals(agency.getHotels().get(0).getName(), borg.getName());
        // try clearing values
        borgLoaded.setAddress(null);
        borgLoaded.getPhoneNumbers().clear();
        borgLoaded.setName(null);
        hotelDbObj = (BasicDBObject) getMorphia().toDBObject(borgLoaded);
        hotels.save(hotelDbObj);
        hotelDbObj = (BasicDBObject) hotels.findOne(new BasicDBObject(Mapper.ID_KEY, hotelDbObj.get(Mapper.ID_KEY)));
        borgLoaded = getMorphia().fromDBObject(Hotel.class, hotelDbObj, new DefaultEntityCache());
        assertNull(borgLoaded.getAddress());
        assertEquals(0, borgLoaded.getPhoneNumbers().size());
        assertNull(borgLoaded.getName());
    }
}
