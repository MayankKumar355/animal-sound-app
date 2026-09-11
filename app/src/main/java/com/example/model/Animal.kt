package com.example.model

import androidx.compose.ui.graphics.Color

enum class AnimalCategory(val displayName: String, val emoji: String) {
    ALL("All", "🐾"),
    WILD("Wild", "🦁"),
    DOMESTIC("Domestic", "🐶"),
    BIRDS("Birds", "🦜")
}

enum class PitchMode(val displayName: String, val emoji: String, val pitchFactor: Float) {
    NORMAL("Normal", "🎵", 1.0f),
    BABY("Baby", "🐭", 1.45f),
    MONSTER("Giant", "🐻", 0.72f)
}

enum class SoundWaveProfile {
    LION_ROAR,
    ELEPHANT_TRUMPET,
    TIGER_GROWL,
    BEAR_GROWL,
    WOLF_HOWL,
    MONKEY_CHATTER,
    SNAKE_HISS,
    FROG_CROAK,
    DOG_BARK,
    CAT_MEOW,
    COW_MOO,
    SHEEP_BAA,
    HORSE_NEIGH,
    PIG_OINK,
    GOAT_MAA,
    DONKEY_HEEHAW,
    ROOSTER_CROW,
    DUCK_QUACK,
    OWL_HOOT,
    PARROT_SQUAWK,
    EAGLE_SCREECH,
    SPARROW_CHIRP,
    PIGEON_COO,
    PENGUIN_HONK
}

data class Animal(
    val id: String,
    val name: String,
    val soundName: String,
    val category: AnimalCategory,
    val emoji: String,
    val funFact: String,
    val habitat: String,
    val soundProfile: SoundWaveProfile,
    val cardColorHex: Long,
    val badgeColorHex: Long
)

object AnimalCatalog {
    val animals = listOf(
        // Wild Animals
        Animal(
            id = "lion",
            name = "Lion",
            soundName = "Roaaar!",
            category = AnimalCategory.WILD,
            emoji = "🦁",
            funFact = "A lion's mighty roar can be heard from 5 miles away!",
            habitat = "African Savanna",
            soundProfile = SoundWaveProfile.LION_ROAR,
            cardColorHex = 0xFFFFE0B2,
            badgeColorHex = 0xFFFB8C00
        ),
        Animal(
            id = "elephant",
            name = "Elephant",
            soundName = "Pawoo-oo!",
            category = AnimalCategory.WILD,
            emoji = "🐘",
            funFact = "Elephants use their long trunks to spray water and give hugs!",
            habitat = "Forests & Grasslands",
            soundProfile = SoundWaveProfile.ELEPHANT_TRUMPET,
            cardColorHex = 0xFFECEFF1,
            badgeColorHex = 0xFF546E7A
        ),
        Animal(
            id = "tiger",
            name = "Tiger",
            soundName = "Grrr-Roar!",
            category = AnimalCategory.WILD,
            emoji = "🐯",
            funFact = "Every single tiger has a unique pattern of stripes!",
            habitat = "Tropical Jungles",
            soundProfile = SoundWaveProfile.TIGER_GROWL,
            cardColorHex = 0xFFFFCC80,
            badgeColorHex = 0xFFE65100
        ),
        Animal(
            id = "bear",
            name = "Bear",
            soundName = "Grooowl!",
            category = AnimalCategory.WILD,
            emoji = "🐻",
            funFact = "Bears have an incredible sense of smell, even better than dogs!",
            habitat = "Deep Woodlands",
            soundProfile = SoundWaveProfile.BEAR_GROWL,
            cardColorHex = 0xFFD7CCC8,
            badgeColorHex = 0xFF6D4C41
        ),
        Animal(
            id = "wolf",
            name = "Wolf",
            soundName = "Awooooo!",
            category = AnimalCategory.WILD,
            emoji = "🐺",
            funFact = "Wolves howl to communicate with their family pack across mountains!",
            habitat = "Tundra & Forests",
            soundProfile = SoundWaveProfile.WOLF_HOWL,
            cardColorHex = 0xFFCFD8DC,
            badgeColorHex = 0xFF37474F
        ),
        Animal(
            id = "monkey",
            name = "Monkey",
            soundName = "Ooh-Ooh Aah!",
            category = AnimalCategory.WILD,
            emoji = "🐵",
            funFact = "Monkeys use their strong tails like an extra arm to swing between branches!",
            habitat = "Rainforest Canopies",
            soundProfile = SoundWaveProfile.MONKEY_CHATTER,
            cardColorHex = 0xFFFFE082,
            badgeColorHex = 0xFFF57F17
        ),
        Animal(
            id = "snake",
            name = "Snake",
            soundName = "Ssssssss!",
            category = AnimalCategory.WILD,
            emoji = "🐍",
            funFact = "Snakes smell the air using their flicking forked tongues!",
            habitat = "Deserts & Jungles",
            soundProfile = SoundWaveProfile.SNAKE_HISS,
            cardColorHex = 0xFFC8E6C9,
            badgeColorHex = 0xFF2E7D32
        ),
        Animal(
            id = "frog",
            name = "Frog",
            soundName = "Ribbit Ribbit!",
            category = AnimalCategory.WILD,
            emoji = "🐸",
            funFact = "Frogs drink water through their skin instead of their mouths!",
            habitat = "Ponds & Wetlands",
            soundProfile = SoundWaveProfile.FROG_CROAK,
            cardColorHex = 0xFFA5D6A7,
            badgeColorHex = 0xFF1B5E20
        ),

        // Domestic Animals
        Animal(
            id = "dog",
            name = "Dog",
            soundName = "Woof Woof!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐶",
            funFact = "Dogs wag their tails to say 'I am so happy to see you!'",
            habitat = "Happy Homes",
            soundProfile = SoundWaveProfile.DOG_BARK,
            cardColorHex = 0xFFFFE0B2,
            badgeColorHex = 0xFFE65100
        ),
        Animal(
            id = "cat",
            name = "Cat",
            soundName = "Meow Meow~",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐱",
            funFact = "Cats purr when they feel safe, cozy, and loved!",
            habitat = "Cozy Couches",
            soundProfile = SoundWaveProfile.CAT_MEOW,
            cardColorHex = 0xFFF8BBD0,
            badgeColorHex = 0xFFC2185B
        ),
        Animal(
            id = "cow",
            name = "Cow",
            soundName = "Moo-oooo!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐮",
            funFact = "Cows have best friends and get happier when they are together!",
            habitat = "Green Pastures",
            soundProfile = SoundWaveProfile.COW_MOO,
            cardColorHex = 0xFFE0E0E0,
            badgeColorHex = 0xFF424242
        ),
        Animal(
            id = "sheep",
            name = "Sheep",
            soundName = "Baa-aaaa!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐑",
            funFact = "Sheep have soft fluffy wool that keeps them warm in the winter!",
            habitat = "Rolling Hills",
            soundProfile = SoundWaveProfile.SHEEP_BAA,
            cardColorHex = 0xFFEDE7F6,
            badgeColorHex = 0xFF5E35B1
        ),
        Animal(
            id = "horse",
            name = "Horse",
            soundName = "Neighhhh!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐴",
            funFact = "Horses can sleep both lying down and standing right up!",
            habitat = "Meadow Ranches",
            soundProfile = SoundWaveProfile.HORSE_NEIGH,
            cardColorHex = 0xFFFFCCBC,
            badgeColorHex = 0xFFD84315
        ),
        Animal(
            id = "pig",
            name = "Pig",
            soundName = "Oink Oink!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐷",
            funFact = "Pigs are super smart animals and love to learn playful tricks!",
            habitat = "Farmyard Barns",
            soundProfile = SoundWaveProfile.PIG_OINK,
            cardColorHex = 0xFFFFCDD2,
            badgeColorHex = 0xFFE53935
        ),
        Animal(
            id = "goat",
            name = "Goat",
            soundName = "Maaa-aaaa!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🐐",
            funFact = "Goats have rectangular pupils that help them spot everything around them!",
            habitat = "Mountain Farms",
            soundProfile = SoundWaveProfile.GOAT_MAA,
            cardColorHex = 0xFFD1C4E9,
            badgeColorHex = 0xFF512DA8
        ),
        Animal(
            id = "donkey",
            name = "Donkey",
            soundName = "Hee-Haw!",
            category = AnimalCategory.DOMESTIC,
            emoji = "🫏",
            funFact = "Donkeys have giant ears to hear sounds from very far away!",
            habitat = "Sunny Fields",
            soundProfile = SoundWaveProfile.DONKEY_HEEHAW,
            cardColorHex = 0xFFE0F2F1,
            badgeColorHex = 0xFF00796B
        ),

        // Birds
        Animal(
            id = "rooster",
            name = "Rooster",
            soundName = "Cock-a-doodle-doo!",
            category = AnimalCategory.BIRDS,
            emoji = "🐓",
            funFact = "Roosters crow early in the morning to welcome the bright sunrise!",
            habitat = "Sunny Farmyards",
            soundProfile = SoundWaveProfile.ROOSTER_CROW,
            cardColorHex = 0xFFFFE082,
            badgeColorHex = 0xFFF57F17
        ),
        Animal(
            id = "duck",
            name = "Duck",
            soundName = "Quack Quack!",
            category = AnimalCategory.BIRDS,
            emoji = "🦆",
            funFact = "Duck feathers are completely waterproof so they stay dry while swimming!",
            habitat = "Sparkling Ponds",
            soundProfile = SoundWaveProfile.DUCK_QUACK,
            cardColorHex = 0xFFB2EBF2,
            badgeColorHex = 0xFF0097A7
        ),
        Animal(
            id = "owl",
            name = "Owl",
            soundName = "Hoo-Hoo-Hooo!",
            category = AnimalCategory.BIRDS,
            emoji = "🦉",
            funFact = "Owls can turn their heads almost all the way around without moving!",
            habitat = "Quiet Night Trees",
            soundProfile = SoundWaveProfile.OWL_HOOT,
            cardColorHex = 0xFFD1C4E9,
            badgeColorHex = 0xFF4527A0
        ),
        Animal(
            id = "parrot",
            name = "Parrot",
            soundName = "Squawk! Hello!",
            category = AnimalCategory.BIRDS,
            emoji = "🦜",
            funFact = "Parrots are famous for mimicking words and songs they hear!",
            habitat = "Tropical Treetops",
            soundProfile = SoundWaveProfile.PARROT_SQUAWK,
            cardColorHex = 0xFFC8E6C9,
            badgeColorHex = 0xFF388E3C
        ),
        Animal(
            id = "eagle",
            name = "Eagle",
            soundName = "Screeech!",
            category = AnimalCategory.BIRDS,
            emoji = "🦅",
            funFact = "Eagles can spot a tiny mouse from a mile high in the sky!",
            habitat = "High Mountain Cliffs",
            soundProfile = SoundWaveProfile.EAGLE_SCREECH,
            cardColorHex = 0xFFFFE0B2,
            badgeColorHex = 0xFFE65100
        ),
        Animal(
            id = "sparrow",
            name = "Sparrow",
            soundName = "Chirp Chirp!",
            category = AnimalCategory.BIRDS,
            emoji = "🐦",
            funFact = "Sparrows take fun little baths in water and dust to keep clean!",
            habitat = "Gardens & Parks",
            soundProfile = SoundWaveProfile.SPARROW_CHIRP,
            cardColorHex = 0xFFB3E5FC,
            badgeColorHex = 0xFF0288D1
        ),
        Animal(
            id = "pigeon",
            name = "Pigeon",
            soundName = "Coo-Coo-Coo!",
            category = AnimalCategory.BIRDS,
            emoji = "🕊️",
            funFact = "Pigeons have amazing memory and can always find their way back home!",
            habitat = "Town Squares",
            soundProfile = SoundWaveProfile.PIGEON_COO,
            cardColorHex = 0xFFE1BEE7,
            badgeColorHex = 0xFF7B1FA2
        ),
        Animal(
            id = "penguin",
            name = "Penguin",
            soundName = "Honk Honk!",
            category = AnimalCategory.BIRDS,
            emoji = "🐧",
            funFact = "Penguins can't fly in the sky, but they fly gracefully underwater!",
            habitat = "Icy Antarctica",
            soundProfile = SoundWaveProfile.PENGUIN_HONK,
            cardColorHex = 0xFFB2DFDB,
            badgeColorHex = 0xFF00695C
        )
    )
}
