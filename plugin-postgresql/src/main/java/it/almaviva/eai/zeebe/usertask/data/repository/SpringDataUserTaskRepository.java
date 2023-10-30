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
package it.almaviva.eai.zeebe.usertask.data.repository;


import it.almaviva.eai.zeebe.usertask.data.entity.TaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataUserTaskRepository extends CrudRepository<TaskEntity, Long> {


  List<TaskEntity> findAllByAssignee(String assignee);

  @Query(
      nativeQuery = true,
      value = "SELECT * FROM TAB_ZUT_TASK WHERE ASSIGNEE is null")
  List<TaskEntity> findAllDemandable();

  void deleteAllByWorkflowInstanceKey(long key);
}
