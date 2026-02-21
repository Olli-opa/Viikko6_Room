# Sääsovellus (Retrofit + Room-välimuisti)

Tämä on sääsovellus, joka on rakennettu Androidille käyttäen Jetpack Composea. Sovellus hakee säädatan OpenWeatherMap API:sta ja käyttää Room-tietokantaa tehokkaana välimuistina API-kutsujen vähentämiseksi.

## Arkkitehtuuri ja datavirta

Projekti noudattaa modernia Android-arkkitehtuuria, joka erottaa vastuualueet selkeisiin kerroksiin.

### 1. Mitä Room tekee? (Entity–DAO–Database)

Room on paikallinen tietokantakirjasto, joka tarjoaa abstraktiokerroksen SQLite-tietokannan päälle. Se mahdollistaa datan tallentamisen laitteelle ja vähentää tarvetta kirjoittaa monimutkaisia SQL-lauseita.

-   **Entity (`WeatherCacheEntity.kt`):** Tämä on Kotlinin `data class`, joka määrittelee tietokannan taulun rakenteen. Jokainen `WeatherCacheEntity`-olio vastaa yhtä riviä `weather_cache`-taulussa ja sisältää kaupungin nimen, säädatan ja aikaleiman.
-   **DAO (`WeatherCacheDao.kt`):** Data Access Object on rajapinta, joka määrittelee tietokantaoperaatiot (esim. `INSERT`, `SELECT`). Kirjoitamme vain funktioiden nimet ja SQL-kyselyt annotaatioina, ja Room generoi tarvittavan koodin automaattisesti.
-   **Database (`AppDatabase.kt`):** Tämä on pääluokka, joka yhdistää tietokannan, entityt ja DAO:t. Se luo varsinaisen tietokanta-instanssin ja tarjoaa pääsyn DAO-rajapintoihin.

### 2. Projektin rakenne

Sovellus on jaettu seuraaviin pääkansioihin:

-   **/data/remote:** Sisältää Retrofit-rajapinnan (`WeatherApi.kt`) verkkokutsuille.
-   **/data/local:** Sisältää Room-tietokannan (`AppDatabase.kt`), DAO:t ja `TypeConverter`-luokan.
-   **/data/model:** Sisältää datamallit: `WeatherResponse` API-vastaukselle ja `WeatherCacheEntity` tietokantataululle.
-   **/data/repository:** Sisältää `WeatherRepository`-luokan, joka toimii ainoana tietolähteenä (`Single Source of Truth`) ViewModelille.
-   **/viewmodel:** Sisältää `WeatherViewModel`-luokan, joka sisältää käyttöliittymän logiikan.
-   **/ui:** Sisältää kaikki Jetpack Compose -käyttöliittymäkomponentit.

### 3. Miten datavirta kulkee?

Datavirta noudattaa yksisuuntaista (Unidirectional Data Flow) mallia:

1.  **UI (`WeatherScreen`):** Käyttäjä syöttää kaupungin ja painaa hakunappia. Tämä kutsuu `WeatherViewModel`-luokan funktiota.
2.  **ViewModel (`WeatherViewModel`):** Vastaanottaa kutsun ja pyytää säädataa `WeatherRepository`-luokalta. ViewModel ei tiedä, tuleeko data verkosta vai paikallisesta välimuistista.
3.  **Repository (`WeatherRepository`):** Tämä on arkkitehtuurin aivot. Se tarkistaa ensin `WeatherCacheDao`-rajapinnan kautta, onko tietokannassa tuoretta dataa.
4.  **Data Sources (DAO & API):**
    -   **Jos DAO palauttaa tuoretta dataa:** Repository palauttaa sen suoraan ViewModelille.
    -   **Jos dataa ei ole tai se on vanhentunutta:** Repository tekee API-kutsun Retrofitilla. Onnistuneen vastauksen jälkeen se tallentaa datan tietokantaan DAO:n avulla ja palauttaa sen sitten ViewModelille.
5.  **Paluu UI:hin:** ViewModel päivittää `StateFlow`-tilansa saamallaan datalla (tai virheellä). Compose UI kuuntelee tätä tilaa ja piirtää itsensä automaattisesti uudelleen näyttääkseen uudet tiedot.

## (Sää) Miten välimuistilogiikka toimii?

Välimuistin logiikka on toteutettu kokonaisuudessaan `WeatherRepository`-luokassa. Se toimii seuraavasti:

1.  Kun `getWeather(city)`-funktiota kutsutaan, se hakee ensin `WeatherCacheDao`-rajapinnan avulla tietokannasta rivin, jonka kaupunki vastaa hakua.
2.  **Jos data löytyy**, se tarkistaa rivin aikaleiman (`timestamp`).
3.  Jos datan ikä on **alle 30 minuuttia**, Repository palauttaa tämän välimuistissa olevan datan, eikä verkkokutsua tehdä lainkaan.
4.  **Jos dataa ei löydy tai se on vanhentunutta** (yli 30 min vanhaa), Repository tekee verkkokutsun OpenWeatherMap API:in.
5.  Onnistuneen vastauksen jälkeen uusi säädata paketoidaan `WeatherCacheEntity`-olioon ja tallennetaan Room-tietokantaan. Tämä korvaa vanhan välimuistidatan kyseiselle kaupungille.
6.  Lopuksi tuore API:sta haettu data palautetaan ViewModelille.

## Demovideo

[Linkki demovideoon](URL_TÄHÄN)
