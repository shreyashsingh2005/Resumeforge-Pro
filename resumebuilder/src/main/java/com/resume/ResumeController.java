package com.resume;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*") 
public class ResumeController {

    private final ResumeService resumeService;

    // Public Constructor lagana best practice hoti hai
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> createResume(@RequestBody Resume resume) {
        try {
            byte[] pdfFile = resumeService.generateAndSaveResume(resume);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            String filename = resume.getName().replaceAll("\\s+", "_") + "_Resume.pdf";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(pdfFile, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public List<Resume> getAllResumes() {
        return resumeService.getAllResumes();
    }
}