package org.factcast.core.subscription;

import static org.factcast.core.TestHelper.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.factcast.core.Fact;
import org.factcast.core.subscription.observer.GenericObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionImplTest {
    @Mock
    private GenericObserver<Fact> observer;

    @InjectMocks
    private SubscriptionImpl<Fact> uut;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        obs = mock(GenericObserver.class);
    }

    @Test
    public void testClose() throws Exception {

        expect(TimeoutException.class, () -> uut.awaitCatchup(10));
        expect(TimeoutException.class, () -> uut.awaitComplete(10));

        uut.close();

        expect(SubscriptionCancelledException.class, () -> uut.awaitCatchup(10));
        expect(SubscriptionCancelledException.class, () -> uut.awaitComplete(10));

    }

    @Test
    public void testAwaitCatchup() throws Exception {

        expect(TimeoutException.class, () -> uut.awaitCatchup(10));
        expect(TimeoutException.class, () -> uut.awaitComplete(10));

        uut.notifyCatchup();

        uut.awaitCatchup();
        expect(TimeoutException.class, () -> uut.awaitComplete(10));

    }

    @Test
    public void testAwaitComplete() throws Exception {

        expect(TimeoutException.class, () -> uut.awaitCatchup(10));
        expect(TimeoutException.class, () -> uut.awaitComplete(10));

        uut.notifyComplete();

        uut.awaitCatchup();
        uut.awaitComplete();
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void testNullConst() throws Exception {
        new SubscriptionImpl<>(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNotifyElementNull() throws Exception {
        uut.notifyElement(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNotifyErrorNull() throws Exception {
        uut.notifyError(null);
    }

    @Test
    public void testOnClose() throws Exception {

        CountDownLatch l = new CountDownLatch(1);

        uut.onClose(() -> l.countDown());

        uut.close();
        l.await();
    }

    private GenericObserver<Integer> obs;

    @Test(expected = NullPointerException.class)
    public void testOnNull() throws Exception {
        SubscriptionImpl.on(null);
    }

    @Test
    public void testOn() throws Exception {
        SubscriptionImpl<Integer> on = SubscriptionImpl.on(obs);

        verify(obs, never()).onNext(any());
        on.notifyElement(1);
        verify(obs).onNext(1);

        verify(obs, never()).onCatchup();
        on.notifyCatchup();
        verify(obs).onCatchup();

        verify(obs, never()).onComplete();
        on.notifyComplete();
        verify(obs).onComplete();

        verify(obs, never()).onError(any());
        on.notifyError(new Throwable("ignore me"));
        verify(obs).onError(any());

    }

    @Test(expected = SubscriptionCancelledException.class)
    public void testOnErrorCompletesFutureCatchup() throws Exception {
        SubscriptionImpl<Integer> on = SubscriptionImpl.on(obs);

        verify(obs, never()).onError(any());
        on.notifyError(new Throwable("ignore me"));
        verify(obs).onError(any());

        on.awaitCatchup();
    }

    @Test(expected = SubscriptionCancelledException.class)
    public void testOnErrorCompletesFutureComplete() throws Exception {
        SubscriptionImpl<Integer> on = SubscriptionImpl.on(obs);

        verify(obs, never()).onError(any());
        on.notifyError(new Throwable("ignore me"));
        verify(obs).onError(any());

        on.awaitComplete();
    }

    @Test(timeout = 100)
    public void testAwaitCatchupLong() throws Exception {
        uut.notifyCatchup();
        uut.awaitCatchup(100000);
    }

    @Test(timeout = 100)
    public void testAwaitCompleteLong() throws Exception {
        uut.notifyComplete();
        uut.awaitComplete(100000);
    }
}
