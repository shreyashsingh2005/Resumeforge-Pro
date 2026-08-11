package com.service;

import com.resume.resumebuilder.model.Resume;
import com.resume.resumebuilder.repository.ResumeRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public byte[] generateAndSaveResume(Resume resumeData) throws Exception {
        // Data ko MySQL me save karna
        resumeRepository.save(resumeData);

        // PDF Create karna
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        
        document.open();
        
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, BaseColor.DARK_GRAY);
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        Paragraph name = new Paragraph(resumeData.getName().toUpperCase(), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        document.add(name);

        Paragraph contact = new Paragraph(resumeData.getEmail() + " | " + resumeData.getPhone(), textFont);
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingAfter(10);
        document.add(contact);

        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(ls));
        
        document.add(new Paragraph(" ", textFont)); 
        document.add(new Paragraph("TECHNICAL SKILLS", headingFont));
        document.add(new Paragraph(resumeData.getSkills(), textFont));
        
        document.add(new Paragraph(" ", textFont));
        document.add(new Chunk(ls));
        
        document.add(new Paragraph(" ", textFont));
        document.add(new Paragraph("PROFESSIONAL EXPERIENCE", headingFont));
        document.add(new Paragraph(resumeData.getExperience(), textFont));

        document.close();
        return out.toByteArray();
    }
}