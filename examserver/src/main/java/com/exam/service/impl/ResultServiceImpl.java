package com.exam.service.impl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.multipart.MultipartFile;

import com.exam.model.User;
import com.exam.model.exam.Quiz;
import com.exam.model.exam.Result;
import com.exam.repo.ResultRepository;
import com.exam.service.ResultService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;



@Service
public class ResultServiceImpl implements ResultService {
	
	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private S3StorageService s3StorageService;
	

	@Override
	public Result getResult(Long rId) {
		// TODO Auto-generated method stub
		Result r=this.resultRepository.findById(rId).get();
		return r;
	}

	
	@Override

	@org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_COMMITTED)
	public Result addResult(Result result) throws IOException {
		// TODO Auto-generated method stub
		Result submittefResult =resultRepository.save(result);
//		long  rId=submittefResult.getrId();
//		Optional<Result> result2=resultRepository.findById(rId);
//		ByteArrayInputStream s3ResultPdf=createResultPdf(submittefResult.getrId());
//		MultipartFile multipartFile = new MockMultipartFile(
//                "file",         // Parameter name (you can change this if needed)
//                "filename.txt", // Original filename
//                "text/plain",   // Content type
//                s3ResultPdf      // InputStream containing the content
//        );
//		String uploadInS3 =	s3StorageService.uploadFile(multipartFile);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
		   result.setDate(now);
		return submittefResult;
	}

	@Override
	public List<Result> allResult(User user_id) {
		// TODO Auto-generated method stub
		List<Result> result=this.resultRepository.findByUser(user_id);
		return result;
	}

	@Override
	public List<Result> allResultByUserAndQuiz(User user_id, Quiz quiz_id) {
		// TODO Auto-generated method stub
		List<Result> resultByQuiz=this.resultRepository.findByUserAndQuiz(user_id, quiz_id);
		return resultByQuiz;
	}

	@Override
	public ByteArrayInputStream createResultPdf(Long rId) {
		// TODO Auto-generated method stub
		//Gettin Result By ID
		Result resultById= this.resultRepository.findById(rId).get();
		
		String title="Welcome to the Quiz World !!!";
		String Content="Pradeep Bairwa";
		String category="Category : ";
		String quiz="Quiz : ";
		String quizDate="Date : ";
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Document document = new Document();
		PdfWriter.getInstance(document, out);
		document.open();
		Font titleFont =  FontFactory.getFont(FontFactory.TIMES_ITALIC,25);
		Paragraph titleParagraph =new Paragraph(title,titleFont);
		titleParagraph.setAlignment(Element.ALIGN_CENTER);
		
		document.add(titleParagraph);
		Font paraFont =  FontFactory.getFont(FontFactory.TIMES_ITALIC);
		Paragraph content =new Paragraph(Content,paraFont);
		content.setAlignment(Element.ALIGN_CENTER);
		document.add(content);
		
		Font catFont =  FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		Paragraph categoryPara =new Paragraph(category,catFont);
		categoryPara.setAlignment(Element.ALIGN_LEFT);
		document.add(categoryPara);
		
		Paragraph quizPara =new Paragraph(quiz,catFont);
		quizPara.setAlignment(Element.ALIGN_LEFT);
		document.add(quizPara);
		
		Paragraph datePara =new Paragraph(quizDate,catFont);
		datePara.setAlignment(Element.ALIGN_RIGHT);
		document.add(datePara);
		
		document.close();
		
		return new ByteArrayInputStream(out.toByteArray());
	}

}
