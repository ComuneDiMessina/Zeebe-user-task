/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.almaviva.eai.zeebe.usertask.data.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity(name = "TAB_ZUT_TASK")
public class TaskEntity {

  @Id
  @Column(name = "KEY", nullable = false)
  private long key;

  @Column(name = "WORKFLOW_INSTANCE_KEY", nullable = false)
  private long workflowInstanceKey;

  @Column(name = "CREATION_TIME", nullable = false)
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime creationTime;

  @Column(name = "ASSIGNMENT_TIME")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime assignmentTime;

  @Column(name = "NAME")
  private String name;

  @Column(name = "VARIABLES", columnDefinition = "text")
  private String variables;

  @Column(name = "FORM_FIELDS", columnDefinition = "text")
  private String formFields;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "JSON_SCHEMA", columnDefinition = "text")
  private String jsonSchema;

  @Column(name = "ASSIGNEE")
  private String assignee;

  @Column(name = "CANDIDATE_GROUPS")
  private String candidateGroups;

  @Column(name = "CANDIDATE_USERS")
  private String candidateUsers;

}
