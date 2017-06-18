package com.ustadmobile.core.networkmanager;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>NetworkTask</h1>
 *
 * This is a class which define network task which might be AcquisitionTask or EntryStatusTask
 *
 *
 * @author kileha3
 */

public abstract class NetworkTask {

    private static final AtomicInteger taskIdAtomicInteger = new AtomicInteger();

    public NetworkTask(NetworkManagerCore networkManager) {
        this.networkManagerCore = networkManager;
        this.taskId = taskIdAtomicInteger.getAndIncrement();
    }

    public NetworkManagerTaskListener managerTaskListener;
    protected NetworkManagerCore networkManagerCore;
    public int queueId;
    public int taskId;
    public int taskType;
    public boolean useBluetooth;
    public boolean useHttp;

    /**
     * Method which initiate network task execution
     */
    public abstract void start();

    /**
     * Method which stop the network task execution
     */
    public abstract void cancel();

    /**
     * method which is used to get ID of the task on the task queue
     * @return int: Task queue id
     */
    public abstract int getQueueId();

    /**
     * Method which used to get task ID
     * @return int: Task ID
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Method which used to get TAsk type, it might be entry AcquisitionTask
     * or EntryStatusTask
     * @return int: Task type flag
     */
    public abstract int getTaskType();
    public void setTaskType(int taskType){
        this.taskType=taskType;
    }

    /**
     * Method which is used to set network manager to manage network operation on the task.
     * @param networkManager NetworkManager instance.
     */
    public void setNetworkManager(NetworkManagerCore networkManager){
        this.networkManagerCore = networkManager;
    }

    /**
     * Method which is use to set network task listener to the network task.
     * @param listener NetworkManagerTaskListener to fire events on right action.
     */
    public void setNetworkTaskListener(NetworkManagerTaskListener listener){
        this.managerTaskListener =listener;
    }

    /**
     * Method which is responsible to tell if the task is allowed to use bluetooth or not.
     * @return boolean: TRUE when allowed, FALSE otherwise.
     */
    public boolean isUseBluetooth() {
        return useBluetooth;
    }

    /**
     * Method which is used to tell the task to use or not use bluetooth in its operation.
     * @param useBluetooth TRUE when allowed, FALSE otherwise.
     */
    public void setUseBluetooth(boolean useBluetooth) {
        this.useBluetooth = useBluetooth;
    }

    /**
     * Method which is responsible to tell if the task is allowed to use HTTP connections or not.
     * @return boolean: TRUE when allowed, FALSE otherwise.
     */
    public boolean isUseHttp() {
        return useHttp;
    }

    /**
     * Method which is used to tell the task to use or not use HTTP connection in its operation.
     * @param useHttp TRUE when allowed, FALSE otherwise.
     */
    public void setUseHttp(boolean useHttp) {
        this.useHttp = useHttp;
    }

}
