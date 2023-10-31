public class old{
    public void testTaskWithVariables() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("NodeName", "TaskName ${task.taskData.processInstanceId}");
        workItem.setParameter("Comment", "Comment for task ${task.id}");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);

        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName " + task.getProcessInstanceId(), task.getName());
        assertEquals(10, task.getPriority().intValue());
        assertEquals("Comment for task " + task.getId(), task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId().intValue());

        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));

        String actualOwner = (String) manager.getResults().get("ActorId");
        assertNotNull(actualOwner);
        assertEquals("Darth Vader", actualOwner);

    }
}
