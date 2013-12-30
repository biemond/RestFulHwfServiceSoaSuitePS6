package nl.amis.soa.workflow.tasks.entities;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Task  {

  public Task() {
  }

  private List<String> assignedTo;
  private String assignedToStr;
  private String acquiredBy;
  private List<String> assignedGroups;
  private Calendar expirationDate;
  private int priority;
  private String taskId;
  private int taskNumber;
  private String processName;
  private String processCategory;
  private String instanceId;
  private String processVersion;
  private String taskDescriptionCode;
  private String title;
  private Calendar created;
  private String state;
  private String outcome;
  private String payloadAsString;
  private String taskType;

  private String lastChangeUser;  
  private Calendar lastChangeDate;  
 


  public void setAssignedTo(List<String> assignedTo) {
      this.assignedTo = assignedTo;
  }

  public List<String> getAssignedTo() {
      return assignedTo;
  }

  public void setAssignedGroups(List<String> assignedGroups) {
      this.assignedGroups = assignedGroups;
  }

  public List<String> getAssignedGroups() {
      return assignedGroups;
  }

  public void setExpirationDate(Calendar expirationDate) {
      this.expirationDate = expirationDate;
  }

  public Calendar getExpirationDate() {
      return expirationDate;
  }


  public void setPriority(int priority) {
      this.priority = priority;
  }

  public int getPriority() {
      return priority;
  }

  public void setTaskId(String taskId) {
      this.taskId = taskId;
  }

  public String getTaskId() {
      return taskId;
  }

  public void setAcquiredBy(String acquiredBy) {
      this.acquiredBy = acquiredBy;
  }

  public String getAcquiredBy() {
      return acquiredBy;
  }

  public void setTaskNumber(int taskNumber) {
      this.taskNumber = taskNumber;
  }

  public int getTaskNumber() {
      return taskNumber;
  }

  public void setProcessName(String processName) {
      this.processName = processName;
  }

  public String getProcessName() {
      return processName;
  }

  public void setInstanceId(String instanceId) {
      this.instanceId = instanceId;
  }

  public String getInstanceId() {
      return instanceId;
  }

  public void setProcessVersion(String processVersion) {
      this.processVersion = processVersion;
  }

  public String getProcessVersion() {
      return processVersion;
  }


  public void setProcessCategory(String processCategory) {
      this.processCategory = processCategory;
  }

  public String getProcessCategory() {
      return processCategory;
  }


  public void setTaskDescriptionCode(String taskDescriptionCode) {
      this.taskDescriptionCode = taskDescriptionCode;
  }

  public String getTaskDescriptionCode() {
      return taskDescriptionCode;
  }


  public void setTitle(String title) {
      this.title = title;
  }

  public String getTitle() {
      return title;
  }

  public void setCreated(Calendar created) {
      this.created = created;
  }

  public Calendar getCreated() {
      return created;
  }

  public void setState(String state) {
      this.state = state;
  }

  public String getState() {
      return state;
  }

  public void setOutcome(String outcome) {
      this.outcome = outcome;
  }

  public String getOutcome() {
      return outcome;
  }

  public void setAssignedToStr(String assignedToStr) {
      this.assignedToStr = assignedToStr;
  }

  public String getAssignedToStr() {
      return assignedToStr;
  }


  public void setPayloadAsString(String payloadAsString) {
      this.payloadAsString = payloadAsString;
  }

  public String getPayloadAsString() {
      return payloadAsString;
  }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }



    public void setLastChangeUser(String lastChangeUser) {
        this.lastChangeUser = lastChangeUser;
    }

    public String getLastChangeUser() {
        return lastChangeUser;
    }

    public void setLastChangeDate(Calendar lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public Calendar getLastChangeDate() {
        return lastChangeDate;
    }
}
