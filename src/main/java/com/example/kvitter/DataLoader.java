package com.example.kvitter;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.services.HashtagService;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
    
    private final UserRepo userRepo;
    private final UserService userService;
    private final KvitterService kvitterService;
    private final HashtagService hashtagService;

    @Value("${insert.dummy.data}")
    private boolean insertDummyData;

    public DataLoader(UserRepo userRepo, UserService userService, KvitterService kvitterService, HashtagService hashtagService) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.kvitterService = kvitterService;
        this.hashtagService = hashtagService;
    }
    
    @Override
    public void run(String... args)  {
        if (insertDummyData) {
            if (userRepo.count() == 0) {
                userService.addUser("mario.bros@nintendo.com", "itsame123", "Mario");
                userService.addUser("lara.croft@tombraider.com", "raider456", "Lara Croft");
                userService.addUser("master.chief@halo.com", "spartan117", "Master Chief");
                userService.addUser("kratos.godofwar@ps.com", "blades789", "Kratos GodofWar");
                userService.addUser("link.hero@hyrule.com", "triforce123", "Link");
                userService.addUser("john.wick@continental.com", "puppy456", "John Wick");
                userService.addUser("tony.stark@avengers.com", "ironman789", "Tony Stark");
                userService.addUser("bruce.wayne@gotham.com", "batman123", "Bruce Wayne");
                userService.addUser("cloud.strife@ffvii.com", "buster456", "Cloud Strife");
                userService.addUser("geralt.rivia@witcher.com", "silver789", "Geralt");
                userService.addUser("arthur.morgan@rdr2.com", "outlaw123", "Arthur Morgan");
                userService.addUser("joel.miller@tlou.com", "guitar456", "Joel Miller");
                userService.addUser("ellie.williams@tlou.com", "clicker789", "Ellie Williams");
                userService.addUser("sarah.connor@terminator.com", "resistance123", "Sarah Connor");
                userService.addUser("neo.matrix@zion.com", "redpill456", "Neo");


                UUID marioId = userService.getUserByEmail("mario.bros@nintendo.com").getId();
                UUID laraId = userService.getUserByEmail("lara.croft@tombraider.com").getId();
                UUID masterChiefId = userService.getUserByEmail("master.chief@halo.com").getId();
                UUID kratosId = userService.getUserByEmail("kratos.godofwar@ps.com").getId();
                UUID linkId = userService.getUserByEmail("link.hero@hyrule.com").getId();
                UUID johnWickId = userService.getUserByEmail("john.wick@continental.com").getId();
                UUID tonyStarkId = userService.getUserByEmail("tony.stark@avengers.com").getId();
                UUID bruceWayneId = userService.getUserByEmail("bruce.wayne@gotham.com").getId();
                UUID cloudStrifeId = userService.getUserByEmail("cloud.strife@ffvii.com").getId();
                UUID geraltRiviaId = userService.getUserByEmail("geralt.rivia@witcher.com").getId();
                UUID arthurMorganId = userService.getUserByEmail("arthur.morgan@rdr2.com").getId();
                UUID joelMillerId = userService.getUserByEmail("joel.miller@tlou.com").getId();
                UUID ellieWilliamsId = userService.getUserByEmail("ellie.williams@tlou.com").getId();
                UUID sarahConnorId = userService.getUserByEmail("sarah.connor@terminator.com").getId();
                UUID neoMatrixId = userService.getUserByEmail("neo.matrix@zion.com").getId();

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



                kvitterService.addKvitter("Just defeated Bowser again. It's-a me, Mario! 🍄", marioId, Arrays.asList(hashtag1, hashtag0));
                kvitterService.addKvitter("Saved the Mushroom Kingdom once again! Now, time for spaghetti. 🍝", marioId, Arrays.asList(hashtag1, hashtag15));
                
                kvitterService.addKvitter("Raiding tombs isn't easy, but someone's gotta do it. 💎", laraId, Arrays.asList(hashtag2, hashtag0));
                kvitterService.addKvitter("Found another ancient artifact. Now, what does this one do? 🗺️", laraId, Arrays.asList(hashtag2, hashtag15));

                kvitterService.addKvitter("Remember: Finish the fight. 🛡️", masterChiefId, Arrays.asList(hashtag3, hashtag0));
                kvitterService.addKvitter("Cortana, where are you? I need your help... again. 🖥️", masterChiefId, Arrays.asList(hashtag3, hashtag15));

                kvitterService.addKvitter("Boy, get over here! Another day, another god to slay. ⚔️", kratosId, Arrays.asList(hashtag4, hashtag0));
                kvitterService.addKvitter("Even gods feel pain. But I’ll keep fighting. 💔", kratosId, Arrays.asList(hashtag4, hashtag15));

                kvitterService.addKvitter("Found the Triforce and saved Zelda... again. 🌟", linkId, Arrays.asList(hashtag5, hashtag0));
                kvitterService.addKvitter("Hyrule is peaceful... for now. Time to relax with Epona. 🐴", linkId, Arrays.asList(hashtag5, hashtag15));

                kvitterService.addKvitter("They took my dog. Big mistake. 🐶💥", johnWickId, Arrays.asList(hashtag6, hashtag15));
                kvitterService.addKvitter("The Continental better not call me again... I need a break. ☕", johnWickId, Arrays.asList(hashtag6, hashtag15));

                kvitterService.addKvitter("I am Iron Man. 💡", tonyStarkId, Arrays.asList(hashtag7, hashtag0));
                kvitterService.addKvitter("Upgraded my suit today. Let’s see how this new arc reactor works. ⚡", tonyStarkId, Arrays.asList(hashtag7, hashtag1));

                kvitterService.addKvitter("Tonight, Gotham is safe. 🦇", bruceWayneId, Arrays.asList(hashtag8, hashtag0));
                kvitterService.addKvitter("Arkham’s criminals are getting smarter. But not smart enough for me. 🕶️", bruceWayneId, Arrays.asList(hashtag8, hashtag15));

                kvitterService.addKvitter("Avalanche is here to save the planet! 🌍", cloudStrifeId, Arrays.asList(hashtag9, hashtag0));
                kvitterService.addKvitter("Midgar can’t hold us down. Avalanche is ready to take on Shinra! 💥", cloudStrifeId, Arrays.asList(hashtag9, hashtag15));

                kvitterService.addKvitter("Witcher for hire. No monsters too big or too small. 🐺", geraltRiviaId, Arrays.asList(hashtag10, hashtag0));
                kvitterService.addKvitter("The path of the Witcher is a lonely one, but destiny calls. 🐉", geraltRiviaId, Arrays.asList(hashtag10, hashtag5));

                kvitterService.addKvitter("Sometimes I miss the gang. Miss you, Hosea. 🤠", arthurMorganId, Arrays.asList(hashtag11, hashtag5));
                kvitterService.addKvitter("Miss the open fields and the sound of horses running wild. 🌾", arthurMorganId, Arrays.asList(hashtag11, hashtag5));

                kvitterService.addKvitter("Ellie, it's just you and me now. 🎸", joelMillerId, Arrays.asList(hashtag12, hashtag2));
                kvitterService.addKvitter("If we’re still breathing, we keep going. That’s all we can do. 🌄", joelMillerId, Arrays.asList(hashtag12, hashtag15));

                kvitterService.addKvitter("Took out another clicker. Not bad, huh? 🔪", ellieWilliamsId, Arrays.asList(hashtag12, hashtag4));
                kvitterService.addKvitter("Just learned another song on my guitar. Wanna hear? 🎶", ellieWilliamsId, Arrays.asList(hashtag12, hashtag15));

                kvitterService.addKvitter("Hasta la vista, baby. 🤖💥", sarahConnorId, Arrays.asList(hashtag13, hashtag7));
                kvitterService.addKvitter("Fate is not set in stone. Keep fighting for the future. 🔩", sarahConnorId, Arrays.asList(hashtag13, hashtag15));

                kvitterService.addKvitter("I chose the red pill. No going back now. 🔴", neoMatrixId, Arrays.asList(hashtag14, hashtag1));
                kvitterService.addKvitter("The Matrix has glitches, but so do I. Stay sharp. ⚙️", neoMatrixId, Arrays.asList(hashtag14, hashtag15));
                
            }
            System.out.println("Dummy data inserted.");
        } else {
            System.out.println("Dummy data insertion skipped.");
        }
    }
}
