package com.example.go4lunchjava;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.chat.ChatMessageModelUi;
import com.example.go4lunchjava.chat.ChatViewModel;
import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.repository.ChatFireStoreRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ChatFireStoreRepository mChatRepo;
    @Mock
    private UsersFireStoreRepository mUsersRepo;

    private ChatViewModel mViewModel;
    private List<ChatMessageModelUi> mUiMessages;

    private String userId1 = "1234";
    private String userId2 = "5678";

    @Before
    public void setUp(){
        mViewModel = new ChatViewModel(mChatRepo, mUsersRepo);

        }

    @Test(expected = RuntimeException.class)
    public void UiMessagesIsNotTriggeredWhenNoMessagesStored() throws InterruptedException {
        //GIVEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>());
        //WHEN
        mViewModel.init("1234", "5678");
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);
        //THEN RunTimeException

    }

    @Test
    public void uiMessageContentsCorrespondToMessagesStored() throws InterruptedException {
        //GIVEN
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(userId1, 56780, "Salut"));
        messages.add(new ChatMessage(userId1, 56780, "Hey"));
        messages.add(new ChatMessage(userId2, 56780, "Hola !"));

        //WHEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>(messages));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());
        mViewModel.init("1234", "5678");
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);

        //THEN
        assertEquals(messages.get(0).getContent(), mUiMessages.get(0).getContent());
        assertEquals(messages.get(1).getContent(), mUiMessages.get(1).getContent());
        assertEquals(messages.get(2).getContent(), mUiMessages.get(2).getContent());

    }

    @Test
    public void firstMessageOfSerieIsMarked() throws InterruptedException {
        //GIVEN
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(userId1, 1575533317248L, "Salut")); //First
        messages.add(new ChatMessage(userId1, 1575533317258L, "Ca va?"));
        messages.add(new ChatMessage(userId2, 1575533317348L, "Bien")); //First
        messages.add(new ChatMessage(userId2, 1575533317548L, "Et toi?"));

        //WHEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>(messages));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());
        mViewModel.init(userId1, userId2);
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);

        //THEN
        assertTrue(mUiMessages.get(0).isFirstOfSerie());
        assertTrue(mUiMessages.get(2).isFirstOfSerie());
        assertFalse(mUiMessages.get(1).isFirstOfSerie());
        assertFalse(mUiMessages.get(3).isFirstOfSerie());
    }

    @Test
    public void timeInMillisTransformedInHumanReadableStringForUi() throws InterruptedException {
        //GIVEN
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(userId1, 1575533317248L, "Salut"));
        messages.add(new ChatMessage(userId1, 1575543717248L, "Hey"));

        //WHEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>(messages));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());
        mViewModel.init(userId1, userId2);
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);

        //THEN
        assertEquals("09:08", mUiMessages.get(0).getTime());
        assertEquals("12:01", mUiMessages.get(1).getTime());
    }

    @Test
    public void uiMessagesAreUpdatedWithPictureUris() throws InterruptedException {
        //GIVEN
        String uri = "phil_picture";

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(userId1, 56780, "Salut"));

        List<User> users = new ArrayList<>();
        users.add(new User(userId1, "Phil", uri, "1", "Burger"));

        //WHEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>(messages));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mViewModel.init(userId1, userId2);
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);

        //THEN
        //TODO NINO: waiting for 2nd source to trigger... (?)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                assertEquals(uri, mUiMessages.get(0).getPictureUri());
            }
        }, 200);

    }

    @Test
    public void picturesUriCorrespondToRightUser() throws InterruptedException {
        //GIVEN
        String uri1 = "phil_picture";
        String uri2 = "anne_picture";

        List<User> users = new ArrayList<>();
        users.add(new User(userId1, "Phil", uri1, "1", "Burger"));
        users.add(new User(userId2, "Anne", uri2, "1", "Burger"));

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(userId1, 56780, "Salut"));
        messages.add(new ChatMessage(userId2, 56780, "Hola !"));

        //WHEN
        when(mChatRepo.getMessagesLiveData()).thenReturn(new MutableLiveData<>(messages));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mViewModel.init(userId1, userId2);
        mUiMessages = LiveDataTestUtil.getOrAwaitValue(mViewModel.uiMessagesLiveData);

        //THEN
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (ChatMessageModelUi uiMessage : mUiMessages) {
                    if (uiMessage.getSenderId().equals(userId1))
                        assertEquals(uri1, uiMessage.getPictureUri());
                    else if (uiMessage.getSenderId().equals(userId2))
                        assertEquals(uri2, uiMessage.getPictureUri());
                }
            }
        }, 200);

    }

}
