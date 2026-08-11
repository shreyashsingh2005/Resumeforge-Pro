package com.resume;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Base64;

@Service
public class ResumeService {
    
    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public byte[] generateAndSaveResume(Resume resumeData) throws Exception {
        resumeRepository.save(resumeData);

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        
        // Premium Fonts
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new BaseColor(80, 80, 80));
        Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font linkFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(37, 99, 235)); // Blue links
        linkFont.setStyle(Font.UNDERLINE);
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(30, 41, 59));
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(51, 65, 85));

        // Layout Table for Header (Photo Left, Details Right)
        PdfPTable headerTable = new PdfPTable(new float[]{1.2f, 4.8f});
        headerTable.setWidthPercentage(100);

        // Cell 1: Profile Photo
        PdfPCell photoCell = new PdfPCell();
        photoCell.setBorder(Rectangle.NO_BORDER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if (resumeData.getProfilePhoto() != null && resumeData.getProfilePhoto().startsWith("data:image")) {
            try {
                String base64Data = resumeData.getProfilePhoto().split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                Image img = Image.getInstance(imageBytes);
                img.scaleToFit(90, 90);
                img.setAlignment(Element.ALIGN_CENTER);
                photoCell.addElement(img);
            } catch (Exception e) {
                System.out.println("Error processing image");
            }
        }
        headerTable.addCell(photoCell);

        // Cell 2: Details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        Paragraph name = new Paragraph(resumeData.getName().toUpperCase(), nameFont);
        detailsCell.addElement(name);
        
        if (resumeData.getTitle() != null && !resumeData.getTitle().isEmpty()) {
            Paragraph title = new Paragraph(resumeData.getTitle(), titleFont);
            title.setSpacingAfter(4);
            detailsCell.addElement(title);
        }

        // Contact & Links Paragraph
        Paragraph contactInfo = new Paragraph();
        contactInfo.setFont(contactFont);
        if (resumeData.getPhone() != null && !resumeData.getPhone().isEmpty()) contactInfo.add(resumeData.getPhone() + "  |  ");
        if (resumeData.getEmail() != null && !resumeData.getEmail().isEmpty()) contactInfo.add(resumeData.getEmail() + "  |  ");
        if (resumeData.getLocation() != null && !resumeData.getLocation().isEmpty()) contactInfo.add(resumeData.getLocation() + "\n");
        
        // Clickable Links
        if (resumeData.getLinkedin() != null && !resumeData.getLinkedin().isEmpty()) {
            Chunk linkedIn = new Chunk("LinkedIn Profile", linkFont);
            linkedIn.setAnchor(resumeData.getLinkedin().startsWith("http") ? resumeData.getLinkedin() : "https://" + resumeData.getLinkedin());
            contactInfo.add(linkedIn);
            contactInfo.add(new Chunk("  |  ", contactFont));
        }
        if (resumeData.getGithub() != null && !resumeData.getGithub().isEmpty()) {
            Chunk github = new Chunk("GitHub", linkFont);
            github.setAnchor(resumeData.getGithub().startsWith("http") ? resumeData.getGithub() : "https://" + resumeData.getGithub());
            contactInfo.add(github);
            contactInfo.add(new Chunk("  |  ", contactFont));
        }
        if (resumeData.getPortfolio() != null && !resumeData.getPortfolio().isEmpty()) {
            Chunk portfolio = new Chunk("Portfolio", linkFont);
            portfolio.setAnchor(resumeData.getPortfolio().startsWith("http") ? resumeData.getPortfolio() : "https://" + resumeData.getPortfolio());
            contactInfo.add(portfolio);
        }
        
        detailsCell.addElement(contactInfo);
        headerTable.addCell(detailsCell);
        document.add(headerTable);

        // Divider
        document.add(new Paragraph(" "));
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(203, 213, 225)); // Light border
        ls.setLineWidth(1.5f);
        document.add(new Chunk(ls));
        
        // Add Sections Safely
        addSection(document, "SUMMARY", resumeData.getSummary(), headingFont, textFont);
        addSection(document, "EDUCATION", resumeData.getEducation(), headingFont, textFont);
        addSection(document, "SKILLS", resumeData.getSkills(), headingFont, textFont);
        addSection(document, "STRENGTHS", resumeData.getStrengths(), headingFont, textFont);
        addSection(document, "PROJECTS", resumeData.getProjects(), headingFont, textFont);
        addSection(document, "EXPERIENCE", resumeData.getExperience(), headingFont, textFont);
        addSection(document, "KEY ACHIEVEMENTS", resumeData.getAchievements(), headingFont, textFont);

        document.close();
        return out.toByteArray();
    }

    private void addSection(Document document, String title, String content, Font headingFont, Font textFont) throws DocumentException {
        if (content != null && !content.trim().isEmpty()) {
            document.add(new Paragraph(" ", textFont));
            Paragraph heading = new Paragraph(title, headingFont);
            heading.setSpacingAfter(4);
            document.add(heading);
            
            Paragraph body = new Paragraph(content, textFont);
            body.setSpacingBefore(2);
            document.add(body);
        }
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }
}