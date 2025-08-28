package com.ummbatin.service_management;

import com.ummbatin.service_management.repositories.*;
import com.ummbatin.service_management.services.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UmmBatinServiceManagementApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private AnnouncementRepository announcementRepository;

	@MockBean
	private WifeRepository wifeRepository;

	@MockBean
	private KindergartenRepository kindergartenRepository;

	@MockBean
	private ChildRepository childRepository;

	@MockBean
	private ComplaintRepository complaintRepository;

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private EnrollmentRepository enrollmentRepository;

	@MockBean
	private EventRepository eventRepository;

	@MockBean
	private AuthenticationResponse authenticationRespons;

	@MockBean
	private  NotificationRepository notificationRepository;

	@MockBean
	private PaymentRepository paymentRepository;

	@MockBean
	private PropertyRepository propertyRepository;

	@MockBean
	private WaterReadingRepository waterReadingRepository;


	@MockBean
	private  PublicServiceRepository publicServiceRepository;

	@MockBean
	private FileStorageService fileStorageService;

	@Test
	void contextLoads() {
	}
}