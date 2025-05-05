package com.example.kvitter;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.services.HashtagService;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepo userRepo;
    private final UserService userService;
    private final KvitterService kvitterService;
    private final HashtagService hashtagService;
    private final UserMapper userMapper;
    private final KvitterRepo kvitterRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${insert.dummy.data}")
    private boolean insertDummyData;

    public DataLoader(UserRepo userRepo, UserService userService, KvitterService kvitterService, HashtagService hashtagService, UserMapper userMapper, KvitterRepo kvitterRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.kvitterService = kvitterService;
        this.hashtagService = hashtagService;
        this.userMapper = userMapper;
        this.kvitterRepo = kvitterRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (insertDummyData) {
            if (userRepo.count() == 0) {

                DetailedUserDto mario = userService.register(new SignUpDto("mario.bros@nintendo.com", "Mario", new char[]{'i', 't', 's', 'a', 'm', 'e', '1', '2', '3'}));
                DetailedUserDto lara = userService.register(new SignUpDto("lara.croft@tombraider.com", "Lara Croft", new char[]{'r', 'a', 'i', 'd', 'e', 'r', '4', '5', '6'}));
                DetailedUserDto chief = userService.register(new SignUpDto("master.chief@halo.com", "Master Chief", new char[]{'s', 'p', 'a', 'r', 't', 'a', 'n', '1', '1', '7'}));
                DetailedUserDto kratos = userService.register(new SignUpDto("kratos.godofwar@ps.com", "Kratos GodOfWar", new char[]{'b', 'l', 'a', 'd', 'e', 's', '7', '8', '9'}));
                DetailedUserDto link = userService.register(new SignUpDto("link.hero@hyrule.com", "Link", new char[]{'t', 'r', 'i', 'f', 'o', 'r', 'c', 'e', '1', '2', '3'}));
                DetailedUserDto wick = userService.register(new SignUpDto("john.wick@continental.com", "John Wick", new char[]{'p', 'u', 'p', 'p', 'y', '4', '5', '6'}));
                DetailedUserDto tony = userService.register(new SignUpDto("tony.stark@avengers.com", "Tony Stark", new char[]{'i', 'r', 'o', 'n', 'm', 'a', 'n', '7', '8', '9'}));
                DetailedUserDto bruce = userService.register(new SignUpDto("bruce.wayne@gotham.com", "Bruce Wayne", new char[]{'b', 'a', 't', 'm', 'a', 'n', '1', '2', '3'}));
                DetailedUserDto cloud = userService.register(new SignUpDto("cloud.strife@ffvii.com", "Cloud Strife", new char[]{'b', 'u', 's', 't', 'e', 'r', '4', '5', '6'}));
                DetailedUserDto geralt = userService.register(new SignUpDto("geralt.rivia@witcher.com", "Geralt", new char[]{'s', 'i', 'l', 'v', 'e', 'r', '7', '8', '9'}));
                DetailedUserDto arthur = userService.register(new SignUpDto("arthur.morgan@rdr2.com", "Arthur Morgan", new char[]{'o', 'u', 't', 'l', 'a', 'w', '1', '2', '3'}));
                DetailedUserDto joel = userService.register(new SignUpDto("joel.miller@tlou.com", "Joel Miller", new char[]{'g', 'u', 'i', 't', 'a', 'r', '4', '5', '6'}));
                DetailedUserDto ellie = userService.register(new SignUpDto("ellie.williams@tlou.com", "Ellie Williams", new char[]{'c', 'l', 'i', 'c', 'k', 'e', 'r', '7', '8', '9'}));
                DetailedUserDto sarah = userService.register(new SignUpDto("sarah.connor@terminator.com", "Sarah Connor", new char[]{'r', 'e', 's', 'i', 's', 't', 'a', 'n', 'c', 'e', '1', '2', '3'}));
                DetailedUserDto neo = userService.register(new SignUpDto("neo.matrix@zion.com", "Neo", new char[]{'r', 'e', 'd', 'p', 'i', 'l', 'l', '4', '5', '6'}));


                Hashtag hashtag0 = hashtagService.addHashTag("Gamin");
                Hashtag hashtag1 = hashtagService.addHashTag("Bowser");
                Hashtag hashtag2 = hashtagService.addHashTag("TombRaider");
                Hashtag hashtag3 = hashtagService.addHashTag("Halo");
                Hashtag hashtag4 = hashtagService.addHashTag("GodOfWar");
                Hashtag hashtag5 = hashtagService.addHashTag("Triforce");
                Hashtag hashtag6 = hashtagService.addHashTag("JohnWick");
                Hashtag hashtag7 = hashtagService.addHashTag("IronMan");
                Hashtag hashtag8 = hashtagService.addHashTag("Batman");
                Hashtag hashtag9 = hashtagService.addHashTag("Avalanche");
                Hashtag hashtag10 = hashtagService.addHashTag("Witcher");
                Hashtag hashtag11 = hashtagService.addHashTag("RDR2");
                Hashtag hashtag12 = hashtagService.addHashTag("Ellie");
                Hashtag hashtag13 = hashtagService.addHashTag("Terminator");
                Hashtag hashtag14 = hashtagService.addHashTag("RedPill");
                Hashtag hashtag15 = hashtagService.addHashTag("Tired");

                LocalDateTime localDateTime = LocalDateTime.now();

                Kvitter kvitter1 = Kvitter.builder().message("Just defeated Bowser again. It's-a me, Mario! 🍄")
                        .user(userMapper.detailedUserDTOToUser(mario)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag1, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter1);

                Kvitter kvitter2 = Kvitter.builder().message("Saved the Mushroom Kingdom once again! Now, time for spaghetti. 🍝")
                        .user(userMapper.detailedUserDTOToUser(mario)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag1, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter2);

                Kvitter kvitter3 = Kvitter.builder().message("Raiding tombs isn't easy, but someone's gotta do it. 💎")
                        .user(userMapper.detailedUserDTOToUser(lara)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag2, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter3);

                Kvitter kvitter4 = Kvitter.builder().message("Found another ancient artifact. Now, what does this one do? 🗺️")
                        .user(userMapper.detailedUserDTOToUser(lara)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag2, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter4);

                Kvitter kvitter5 = Kvitter.builder().message("Remember: Finish the fight. 🛡️")
                        .user(userMapper.detailedUserDTOToUser(chief)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag3, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter5);

                Kvitter kvitter6 = Kvitter.builder().message("Cortana, where are you? I need your help... again. 🖥️")
                        .user(userMapper.detailedUserDTOToUser(chief)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag3, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter6);

                Kvitter kvitter7 = Kvitter.builder().message("Boy, get over here! Another day, another god to slay. ⚔️")
                        .user(userMapper.detailedUserDTOToUser(kratos)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag4, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter7);

                Kvitter kvitter8 = Kvitter.builder().message("Even gods feel pain. But I’ll keep fighting. 💔")
                        .user(userMapper.detailedUserDTOToUser(kratos)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag4, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter8);

                Kvitter kvitter9 = Kvitter.builder().message("Found the Triforce and saved Zelda... again. 🌟")
                        .user(userMapper.detailedUserDTOToUser(link)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag5, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter9);

                Kvitter kvitter10 = Kvitter.builder().message("Hyrule is peaceful... for now. Time to relax with Epona. 🐴")
                        .user(userMapper.detailedUserDTOToUser(link)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag5, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter10);

                Kvitter kvitter11 = Kvitter.builder().message("They took my dog. Big mistake. 🐶💥")
                        .user(userMapper.detailedUserDTOToUser(wick)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag6, hashtag15)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter11);

                Kvitter kvitter12 = Kvitter.builder().message("The Continental better not call me again... I need a break. ☕")
                        .user(userMapper.detailedUserDTOToUser(wick)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag6, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter12);

                Kvitter kvitter13 = Kvitter.builder().message("I am Iron Man. 💡")
                        .user(userMapper.detailedUserDTOToUser(tony)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag7, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter13);

                Kvitter kvitter14 = Kvitter.builder().message("Upgraded my suit today. Let’s see how this new arc reactor works. ⚡")
                        .user(userMapper.detailedUserDTOToUser(tony)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag7, hashtag1)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter14);

                Kvitter kvitter15 = Kvitter.builder().message("Tonight, Gotham is safe. 🦇")
                        .user(userMapper.detailedUserDTOToUser(bruce)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag8, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter15);

                Kvitter kvitter16 = Kvitter.builder().message("Arkham’s criminals are getting smarter. But not smart enough for me. 🕶️")
                        .user(userMapper.detailedUserDTOToUser(bruce)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag8, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter16);

                Kvitter kvitter17 = Kvitter.builder().message("Avalanche is here to save the planet! 🌍")
                        .user(userMapper.detailedUserDTOToUser(cloud)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag9, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter17);

                Kvitter kvitter18 = Kvitter.builder().message("Midgar can’t hold us down. Avalanche is ready to take on Shinra! 💥")
                        .user(userMapper.detailedUserDTOToUser(cloud)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag9, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter18);

                Kvitter kvitter19 = Kvitter.builder().message("Witcher for hire. No monsters too big or too small. 🐺")
                        .user(userMapper.detailedUserDTOToUser(geralt)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag10, hashtag0)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter19);

                Kvitter kvitter20 = Kvitter.builder().message("The path of the Witcher is a lonely one, but destiny calls. 🐉")
                        .user(userMapper.detailedUserDTOToUser(geralt)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag10, hashtag5)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter20);

                Kvitter kvitter21 = Kvitter.builder().message("Sometimes I miss the gang. Miss you, Hosea. 🤠")
                        .user(userMapper.detailedUserDTOToUser(arthur)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag11, hashtag5)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter21);

                Kvitter kvitter22 = Kvitter.builder().message("Miss the open fields and the sound of horses running wild. 🌾")
                        .user(userMapper.detailedUserDTOToUser(arthur)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag11, hashtag0)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter22);

                Kvitter kvitter23 = Kvitter.builder().message("Ellie, it's just you and me now. 🎸")
                        .user(userMapper.detailedUserDTOToUser(joel)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag12, hashtag2)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter23);

                Kvitter kvitter24 = Kvitter.builder().message("If we’re still breathing, we keep going. That’s all we can do. 🌄")
                        .user(userMapper.detailedUserDTOToUser(joel)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag12, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter24);

                Kvitter kvitter25 = Kvitter.builder().message("Took out another clicker. Not bad, huh? 🔪")
                        .user(userMapper.detailedUserDTOToUser(ellie)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag12, hashtag4)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter25);

                Kvitter kvitter26 = Kvitter.builder().message("Just learned another song on my guitar. Wanna hear? 🎶")
                        .user(userMapper.detailedUserDTOToUser(ellie)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag12, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter26);

                Kvitter kvitter27 = Kvitter.builder().message("Hasta la vista, baby. 🤖💥")
                        .user(userMapper.detailedUserDTOToUser(sarah)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag13, hashtag7)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter27);

                Kvitter kvitter28 = Kvitter.builder().message("Fate is not set in stone. Keep fighting for the future. 🔩")
                        .user(userMapper.detailedUserDTOToUser(sarah)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag13, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter28);

                Kvitter kvitter29 = Kvitter.builder().message("I chose the red pill. No going back now. 🔴")
                        .user(userMapper.detailedUserDTOToUser(neo)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag14, hashtag1)).isPrivate(true).isActive(true).build();
                kvitterRepo.save(kvitter29);

                Kvitter kvitter30 = Kvitter.builder().message("The Matrix has glitches, but so do I. Stay sharp. ⚙️")
                        .user(userMapper.detailedUserDTOToUser(neo)).createdDateAndTime(localDateTime)
                        .hashtags(Arrays.asList(hashtag14, hashtag15)).isPrivate(false).isActive(true).build();
                kvitterRepo.save(kvitter30);

                System.out.println("Dummy data inserted.");
            }
            System.out.println("Dummy data insertion skipped.");
        }
    }
}
