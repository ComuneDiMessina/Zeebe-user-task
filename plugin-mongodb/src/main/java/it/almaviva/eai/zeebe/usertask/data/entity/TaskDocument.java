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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Document(collection = "task")
public class TaskDocument {

  @Indexed(name = "key")
  private long key;

  private LocalDateTime creationTime;

  private LocalDateTime assignmentTime;

  private String name;

  private String variables;

  private String formFields;

  private String description;

  private String jsonSchema;

  @Indexed(name = "assignee")
  private String assignee;

  @Indexed(name = "candidateGroups")
  private String candidateGroups;

  @Indexed(name = "candidateUsers")
  private String candidateUsers;

}
