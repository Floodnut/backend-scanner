package scanner.service.history;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import scanner.dto.history.VisualDto;
import scanner.dto.report.ScanHistoryDetailDto;
import scanner.dto.report.ScanSummaryDto;
import scanner.model.enums.Language;
import scanner.model.history.ScanHistory;
import scanner.model.history.ScanHistoryDetail;
import scanner.repository.ScanHistoryDetailsRepository;
import scanner.repository.ScanHistoryRepository;
import scanner.dto.report.ReportResponse;

@Service
@RequiredArgsConstructor
public class ScanHistoryService {

	private final ScanHistoryRepository scanHistoryRepository;
	private final ScanHistoryDetailsRepository scanHistoryDetailsRepository;

	public List<ScanHistory> getHistoryList() {
		return scanHistoryRepository.findTop10ByOrderByHistorySeqDesc();
	}

	@Transactional
	public ReportResponse getReportDetails(Long reportId, Language lang) {

		ScanHistory history = scanHistoryRepository.findByHistorySeq(reportId);
		List<ScanHistoryDetail> details = scanHistoryDetailsRepository.findByHistorySeq(reportId);
		ScanSummaryDto summaryDto = ScanSummaryDto.toLangDto(history, lang);

		List<ScanHistoryDetailDto> detailsDto;
		if (lang == Language.KOREAN)
			detailsDto = details.stream().map(ScanHistoryDetailDto::toKorDto).collect(Collectors.toList());
		else
			detailsDto = details.stream().map(ScanHistoryDetailDto::toEngDto).collect(Collectors.toList());

		return new ReportResponse(summaryDto, detailsDto);
	}

	public VisualDto.Response getVisualization(Long reportId) {

		ScanHistory history = scanHistoryRepository.findByHistorySeq(reportId);

		return new VisualDto.Response(history.getHistorySeq(), history.getCreatedAt(), history.getVisual());
	}
}
