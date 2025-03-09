package com.example.demo.member.chat.service;

import com.example.demo.member.chat.domain.ChatMessage;
import com.example.demo.member.chat.domain.ChatParticipant;
import com.example.demo.member.chat.domain.ChatRoom;
import com.example.demo.member.chat.domain.ReadStatus;
import com.example.demo.member.chat.dto.ChatMessageDto;
import com.example.demo.member.chat.dto.ChatRoomListResDto;
import com.example.demo.member.chat.dto.MyChatListResDto;
import com.example.demo.member.chat.repository.ChatMessageRepository;
import com.example.demo.member.chat.repository.ChatParticipantRepository;
import com.example.demo.member.chat.repository.ChatRoomRepository;
import com.example.demo.member.chat.repository.ReadStatusRepository;
import com.example.demo.member.domain.Member;
import com.example.demo.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public void saveMessage(Long roomId, ChatMessageDto chatMessageReqDto) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("rooms cant not be founded"));
        // 보낸사람 조회
        Member sender = memberRepository.findByEmail(chatMessageReqDto.getSenderEmail()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        // 메세지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageReqDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        // 사용자별로 읽음 여부 저장
        List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (var chatParticipant : participants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(chatParticipant.getMember())
                    .chatMessage(chatMessage)
                    .isRead(chatParticipant.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    public void createGroupRoom(String roomName) {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();

        chatRoomRepository.save(chatRoom);
        // 채팅 참여자로 개설자 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomListResDto> getGroupRoomList() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            ChatRoomListResDto chatRoomListResDto = ChatRoomListResDto.builder()
                    .roomId(chatRoom.getId())
                    .roomName(chatRoom.getName())
                    .build();
            dtos.add(chatRoomListResDto);
        }
        return dtos;
    }

    public void addParticipantToGroupChat(Long roomId) {
        //채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new EntityNotFoundException("rooms can not be founded")
        );

        //member 조회
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("members can not be founded"));

        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("this is not a group chat room");
        }

        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        //이미 참여자인지 검증
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, member);
        }
        //ChatParticipant 객체 생성 후 저장
    }

    public void addParticipantToRoom(ChatRoom chatRoom, Member member) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        // 내가 해당 채팅방의 참여자가 아닐 경우 에러
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("rooms can not be founded"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check = false;

        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member)) {
                check = true;
            }
        }
        if (!check) throw new IllegalArgumentException("you are not a participant of this room");

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .message(chatMessage.getContent())
                    .senderEmail(chatMessage.getMember().getEmail())
                    .build();
            chatMessageDtos.add(chatMessageDto);
        }
        // 특정 room에 대한 message 조회
        return chatMessageDtos;
    }

    public boolean isRoomParticipant(String email, long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("rooms can not be founded"));
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant chatParticipant : chatParticipants) {
            if (chatParticipant.getMember().equals(member)) {
                return true;
            }
        }
        return false;
    }

    public void messageRead(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("rooms can not be founded"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);

        for (ReadStatus readStatus : readStatuses) {
            readStatus.updateIsRead(true);
        }
    }

    public List<MyChatListResDto> getMyChatRooms() {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResDto> myChatListResDtos = new ArrayList<>();

        for (var c : chatParticipants) {

            Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);

            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            myChatListResDtos.add(dto);
        }

        return myChatListResDtos;
    }

    public void leaveGroupRoom(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("rooms can not be founded"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));


        if (chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("this is not a group chat room");
        }

        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(() -> new EntityNotFoundException("participants can not be founded"));
        chatParticipantRepository.delete(chatParticipant);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        if (chatParticipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }

    }

    public Long getOrCreatePrivateRoom(Long otherMemberId) {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));
        Member otherMember = memberRepository.findById(otherMemberId).orElseThrow(() -> new EntityNotFoundException("members can not be founded"));

        // 나와 상대방이 1:1 채팅에 이미 참석하고 있다면 해당 roomId return
        Optional<ChatRoom>  chatRoom = chatParticipantRepository.findExistingPrivateRoom(member.getId(), otherMember.getId());
        if (chatRoom.isPresent()) {
            return chatRoom.get().getId();
        }
        // 만약에 1:1 채팅방이 없는 경우 기존 채팅방 개설
        ChatRoom newChatRoom = ChatRoom.builder()
                .isGroupChat("N")
                .name(member.getEmail() + "-" + otherMember.getEmail())
                .build();
         chatRoomRepository.save(newChatRoom);
        // 두 사람 모두 참여자로 새롭게 추가
        addParticipantToRoom(newChatRoom, member);
        addParticipantToRoom(newChatRoom, otherMember);

        return newChatRoom.getId();

    }
}

