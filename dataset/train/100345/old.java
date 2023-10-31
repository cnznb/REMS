public class old{
    public void testProcessPeopleAssignments() {
		List<OrganizationalEntity> organizationalEntities = new ArrayList<OrganizationalEntity>();

		String ids = "espiegelberg,   drbug   ";
		assertTrue(organizationalEntities.size() == 0);		
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 2);
		organizationalEntities.contains("drbug");
		organizationalEntities.contains("espiegelberg");
		assertTrue(organizationalEntities.get(0) instanceof User);
		assertTrue(organizationalEntities.get(1) instanceof User);

		ids = null;
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.size() == 0);		
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 0);

		ids = "     ";
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.size() == 0);		
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, true);
		assertTrue(organizationalEntities.size() == 0);

		ids = "Software Developer";
		organizationalEntities = new ArrayList<OrganizationalEntity>();
		assertTrue(organizationalEntities.size() == 0);		
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
		assertTrue(organizationalEntities.size() == 1);
		assertTrue(organizationalEntities.get(0) instanceof Group);

		// Test that a duplicate is not added; only 1 of the 2 passed in should be added
		ids = "Software Developer,Project Manager";
		peopleAssignmentHelper.processPeopleAssignments(ids, organizationalEntities, false);
		assertTrue(organizationalEntities.size() == 2);
		assertTrue(organizationalEntities.get(0) instanceof Group);
		assertTrue(organizationalEntities.get(1) instanceof Group);

	}
}
