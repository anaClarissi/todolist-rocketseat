package com.anaClarissi.todolist.task;

import com.anaClarissi.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {

            return ResponseEntity.status(400).body("A Data de Início / Término de ser Maior do que a Data Atual");

        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {

            return ResponseEntity.status(400).body("A Data de Início deve ser Menor do que a Data de Término");

        }

        var task = this.taskRepository.save(taskModel);

        return ResponseEntity.status(200).body(task);

    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");

        var tasks = this.taskRepository.findByIdUser((UUID) idUser);

        return tasks;

    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {

            return ResponseEntity.status(400).body("Tarefa não encontrada");

        }

        var idUser = request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)) {

            return ResponseEntity.status(400).body("Usuario não tem Permissão");

        }

        Utils.copyNoNullProperties(taskModel, task);

        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);

    }

}
