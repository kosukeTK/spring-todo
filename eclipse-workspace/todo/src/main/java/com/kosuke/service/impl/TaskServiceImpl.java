package com.kosuke.service.impl;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec.ContentTypeOptionsSpec;
import org.springframework.stereotype.Service;

import com.kosuke.config.Property;
import com.kosuke.model.Task;
import com.kosuke.repository.TaskRepository;
import com.kosuke.service.TaskService;

import javax.transaction.Transactional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The TaskServiceImpl class
 *
 * @author kosuke takeuchi
 * @version 1.0
 * Date 2021/8/15.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private Property property;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Boolean delete(int id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Task update(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(int id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public List<Task> findAll() {
        return (List<Task>) taskRepository.findAll();
    }

    @Override
    public List<Task> findByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public List<Task> findByUserIdStatus(int userId, String status) {
        //return  taskRepository.findByUserIdStatus(userId, status);
        return  taskRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Task> findBetween(int start, int end) {
        return taskRepository.findBetween(start, end);
    }
    
    
    /**
     *
     * ???????????????????????????ID???MAX?????????
     * @param	userId
     * @return	maxTaskId
     */
    @Override
    public int findMaxTaskId(int userId) {
    	return taskRepository.findMaxTaskId(userId);
    }
    
	/**
	 *
	 * ??????????????????????????????
	 *
	 * @param reqTask
	 * @return 
	 */
	@Override
	public void uploadTaskImage(Task reqTask) {
		//?????????????????????????????????????????????
		String baseDir = property.getBaseDir();
		String userId = Integer.toString(reqTask.getUserId());
		String fileName = reqTask.getTaskImage().getOriginalFilename().toString();
		//?????????ID?????????
		String taskId = Integer.toString(findMaxTaskId(reqTask.getUserId()) + 1);
		//??????????????????????????????
		if (reqTask.getTaskImage().isEmpty()) {
			return;
		}
		//??????????????????????????????
		if (!Arrays.asList(
				ContentType.IMAGE_JPEG.getMimeType(),
				ContentType.IMAGE_PNG.getMimeType()).contains(reqTask.getTaskImage().getContentType())) {
			return;
		}
		try {
			//???????????????????????????????????????
			File uploadUserDirFile = new File(baseDir, userId);
			if(!uploadUserDirFile.exists()) {
				uploadUserDirFile.mkdir();
			}
			//????????????????????????????????????
			File uploadDirFile = new File(uploadUserDirFile, taskId);
			if(!uploadDirFile.exists()) {
				uploadDirFile.mkdir();
			}
			//?????????????????????????????????
			File uploadFile = new File(uploadDirFile + "/" + fileName);
			byte[] bytes = reqTask.getTaskImage().getBytes();
			BufferedOutputStream uploadFileStream = 
					new BufferedOutputStream(new FileOutputStream(uploadFile));
			uploadFileStream.write(bytes);
			uploadFileStream.close();
		} catch (Exception e) {
			System.out.println("????????????????????????????????????");
		}
		
		
		
	}
}
