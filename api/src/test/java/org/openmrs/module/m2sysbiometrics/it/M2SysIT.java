package org.openmrs.module.m2sysbiometrics.it;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricSensitiveTestBase;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.bioplugin.LocalBioServerClient;
import org.openmrs.module.m2sysbiometrics.bioplugin.NationalBioServerClient;
import org.openmrs.module.m2sysbiometrics.client.M2SysV105Client;
import org.openmrs.module.m2sysbiometrics.http.M2SysHttpClient;
import org.openmrs.module.registrationcore.api.biometrics.model.BiometricSubject;
import org.springframework.beans.factory.annotation.Autowired;

public class M2SysIT extends M2SysBiometricSensitiveTestBase {

    private static final int CAPTURE_TIMEOUT = 120;
    private static final int LOCATION_ID = 1;

    @Autowired
    private M2SysHttpClient m2SysHttpClient;

    @Autowired
    private M2SysV105Client client;

    @Autowired
    private AdministrationService adminService;

    @Autowired
    private NationalBioServerClient nationalBioServerClient;

    @Autowired
    private LocalBioServerClient localBioServerClient;

    @Autowired
    private PatientService patientService;

    @Before
    public void setUp() {
        String custKey = System.getenv("m2sys-biometrics.customKey");
        String password = System.getenv("m2sys-biometrics.server.password");
        String username = System.getenv("m2sys-biometrics.server.user");
        String accessPointId = System.getenv("m2sys-biometrics.accessPointID");
        String apiUrl = System.getenv("m2sys-biometrics.server.url");

        String localServiceUrl = System.getenv("m2sys-biometrics.local-service.url");
        String nationalServiceUrl = System.getenv("m2sys.biometrics.national-service.url");
        String nationalServiceUsername = System.getenv("m2sys.biometrics.national-service.username");
        String nationalServicePassword = System.getenv("m2sys.biometrics.national-service.password");

        //adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CUSTOMER_KEY, custKey);
       // adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_PASSWORD, password);
        //adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_USERNAME, username);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CLOUD_SCANR_URL, apiUrl);
       // adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_ACCESS_POINT_ID, accessPointId);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_CAPTURE_TIMEOUT,
                String.valueOf(CAPTURE_TIMEOUT));
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCATION_ID,
                String.valueOf(LOCATION_ID));

        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_LOCAL_SERVICE_URL,
                localServiceUrl);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_SERVICE_URL,
                 nationalServiceUrl);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_USERNAME,
                nationalServiceUsername);
        adminService.setGlobalProperty(M2SysBiometricsConstants.M2SYS_NATIONAL_PASSWORD,
                nationalServicePassword);
    }

    @Test
    //@Ignore
    public void test() {
        BiometricSubject subject = new BiometricSubject("SEARCH_TEST2");
        //engine.delete(subject.getSubjectId());
        //engine.enroll(subject);

        //engine.delete(subject.getSubjectId());
        //engine.enroll(subject);

        localBioServerClient.delete("0da5aa64-1cbf-490f-9053-ca4076609f07");
        nationalBioServerClient.delete("NFP_TEST");
        localBioServerClient.delete("SEARCH_TEST2");
        nationalBioServerClient.delete("SEARCH_TEST2");


        //String response = nationalBioServerClient.identify(biometricXml);
        //response = localBioServerClient.identify(biometricXml);

        // local 0da5aa64-1cbf-490f-9053-ca4076609f07
        // national NFP_TEST

        client.enroll(subject);
    }
}
