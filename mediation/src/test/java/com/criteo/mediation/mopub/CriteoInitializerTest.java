package com.criteo.mediation.mopub;

import static com.mopub.common.privacy.ConsentStatus.EXPLICIT_YES;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoInitException;
import com.mopub.common.privacy.PersonalInfoManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoInitializerTest {

  @Mock
  private PersonalInfoManager personalInfoManager;

  @InjectMocks
  private CriteoInitializer criteoInitializer;

  @Mock
  private Context context;

  @Mock
  private Criteo.Builder criteoBuilder;
  @Mock
  private Criteo criteo;

  private CriteoInitializer criteoInitializerSpy;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    criteoInitializerSpy = spy(criteoInitializer);
  }

  @Test
  public void testInit() throws Exception {
    // given
    String publisherId = "fake_publisher_id";
    when(personalInfoManager.getPersonalInfoConsentStatus()).thenReturn(EXPLICIT_YES);
    when(criteoBuilder.init()).thenReturn(criteo);
    doReturn(criteoBuilder).when(criteoInitializerSpy).getCriteoBuilder(context, publisherId);

    // when
    criteoInitializerSpy.init(context, publisherId);

    // then
    verify(criteo).setMopubConsent(EXPLICIT_YES.name());
    verify(criteoBuilder).mopubConsent(EXPLICIT_YES.name());
  }

  @Test
  public void testInit_WhenThrowsCriteoInitException_ItShouldNotCrash() throws Exception {
    // given
    String publisherId = "fake_publisher_id";
    when(personalInfoManager.getPersonalInfoConsentStatus()).thenReturn(EXPLICIT_YES);
    when(criteoBuilder.init()).thenThrow(CriteoInitException.class);
    doReturn(criteoBuilder).when(criteoInitializerSpy).getCriteoBuilder(context, publisherId);


    // when
    try {
      criteoInitializerSpy.init(context, publisherId);
    } catch (Exception e) {
      fail("Exception shouldn't be thrown when initializing Criteo");
    }

    // then
    verify(criteo, never()).setMopubConsent(anyString());
    verify(criteoBuilder).mopubConsent(EXPLICIT_YES.name());
  }

  @Test
  public void testInit_GivenNullPersonalInfoManager_ItShouldNotCrash() throws Exception {
    // given
    personalInfoManager = null;
    criteoInitializer = spy(new CriteoInitializer(personalInfoManager));
    String publisherId = "fake_publisher_id";
    when(criteoBuilder.init()).thenReturn(criteo);
    doReturn(criteoBuilder).when(criteoInitializer).getCriteoBuilder(context, publisherId);

    // when
    criteoInitializer.init(context, publisherId);

    // then
    verify(criteo).setMopubConsent(null);
    verify(criteoBuilder).mopubConsent(null);
  }
}
