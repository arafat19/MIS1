package com.athena.mis


public interface ActionIntf {


    // Check the pre condition(if any) of corresponding ActionService
    public abstract Object executePreCondition(Object parameters, Object obj);

    // execute post condition(if any) of corresponding ActionService
    public abstract Object executePostCondition(Object parameters, Object obj);

    /**
     * Executes the action and returns the result of this execution
     *
     * @param params Request parameters
     * @param obj Additional parameters if need be, it can be a map of key/value
     * @return Returns the execution result
     */
    public abstract Object execute(Object parameters, Object obj);

    /**
     * Builds UI specific object upon success; e.g., wrapping an object in GridEntity for
     * displaying in a Grid view.
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public abstract Object buildSuccessResultForUI(Object obj);

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public abstract Object buildFailureResultForUI(Object obj);


}