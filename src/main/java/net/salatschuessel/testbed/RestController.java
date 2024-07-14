package net.salatschuessel.testbed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.spring.guides.gs_producing_web_service.CountriesPort;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import net.salatschuessel.testbed.persistence.RemoteServiceLogRepository;

@Controller
public class RestController {

	private final CountriesPort countriesPort;
	private final RemoteServiceLogRepository remoteServiceLogRepository;

	@Autowired
	public RestController(final CountriesPort countriesPort,
			final RemoteServiceLogRepository remoteServiceLogRepository) {
		this.countriesPort = countriesPort;
		this.remoteServiceLogRepository = remoteServiceLogRepository;
	}

	@GetMapping("/rest/triggerWs")
	public ResponseEntity<String> triggerWs() {
		final GetCountryRequest request = new GetCountryRequest();
		request.setName("Spain");

		this.countriesPort.getCountry(request);
//
//		final var maxId = this.remoteServiceLogRepository.selectMaxId();
//		if (maxId != null) {
//			final var remoteServiceLog = this.remoteServiceLogRepository.selectGenericRowMapper(maxId);
//
//			return new ResponseEntity<>(remoteServiceLog.body(), HttpStatus.OK);
//		} else {
		return ResponseEntity.noContent().build();
//		}
	}

}
