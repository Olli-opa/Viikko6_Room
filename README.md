# Viikkotehtävä 5: Sääsovellus (Retrofit + OpenWeather API)

Tämä on yksinkertainen sääsovellus, joka on rakennettu Androidille käyttäen Jetpack Composea. Sovellus hakee säädatan OpenWeatherMap API:sta ja näyttää sen käyttäjälle.

## Tekninen toteutus

### Mitä Retrofit tekee?

Retrofit on HTTP-asiakasohjelma Androidille ja Javalle. Tässä projektissa se huolehtii verkkopyyntöjen tekemisestä OpenWeatherMap API:n rajapintaan. Retrofitin avulla voimme määritellä API-kutsut selkeästi Kotlin-rajapinnan (`WeatherApi.kt`) kautta ja hoitaa pyyntöjen lähettämisen ja vastausten vastaanottamisen helposti.

### Miten JSON muutetaan dataluokiksi?

Kun Retrofit saa vastauksen API:lta, vastaus on JSON-muodossa. Käytämme `GsonConverterFactory`-kirjastoa, joka on Retrofitin lisäosa. Gson hoitaa automaattisesti tämän JSON-datan muuntamisen (deserialisoinnin) Kotlinin `data class` -luokiksi (kuten `WeatherResponse.kt`). Tämä tarkoittaa, että voimme käsitellä saamaamme dataa suoraan tyyppiturvallisina Kotlin-olioina ilman manuaalista JSON-jäsennystä.

### Miten coroutines (korutiinit) toimivat tässä?

API-kutsut ovat I/O-operaatioita, jotka voivat kestää hetken. Niitä ei saa koskaan suorittaa sovelluksen pääsäikeessä (UI-säie), koska se jäädyttäisi käyttöliittymän. Kotlinin korutiinit mahdollistavat asynkronisen koodin suorittamisen siististi.

1.  **Taustasäie:** `WeatherViewModel`-luokassa `viewModelScope.launch` käynnistää uuden korutiinin, joka suorittaa API-kutsun taustasäikeessä.
2.  **UI-päivitys:** Kun API-kutsu on valmis ja data on vastaanotettu, tulos päivitetään `StateFlow`-tilaan. Koska Compose-näkymä tarkkailee tätä tilaa, käyttöliittymä päivittyy automaattisesti näyttämään uuden datan. Tämä päivitys tapahtuu turvallisesti pääsäikeessä.

### Miten UI-tila toimii?

Käytämme modernia Unidirectional Data Flow (UDF) -arkkitehtuuria.

1.  **ViewModel hallitsee tilaa:** `WeatherViewModel` sisältää `WeatherUiState`-nimisen `data class` -olion, joka edustaa kaikkea, mitä käyttöliittymässä voidaan näyttää (lataustila, virhe, säädata).
2.  **Tila on StateFlow:** Tämä tila on kääritty `StateFlow`-olioon, joka on havaittavissa oleva tietovirta.
3.  **Compose reagoi muutoksiin:** `WeatherScreen`-komponentti käyttää `collectAsState()`-funktiota kuunnellakseen tätä tilaa. Aina kun `WeatherUiState` muuttuu ViewModelissa, Compose-funktio piirretään automaattisesti uudelleen heijastamaan uutta tilaa. Tämä tekee tilanhallinnasta ennustettavaa ja helppoa.

### Miten API-avain on tallennettu?

API-avainta ei koskaan tallenneta suoraan koodiin tai versionhallintaan turvallisuussyistä. Tässä projektissa sen hallinta tapahtuu seuraavasti:

1.  **`local.properties`:** Avain on tallennettu tähän tiedostoon, joka on vain paikallisella koneellasi ja jonka Git jättää huomiotta (`.gitignore`-tiedoston ansiosta).
2.  **`build.gradle.kts`:** Sovelluksen build-prosessin aikana Gradle lukee avaimen `local.properties`-tiedostosta.
3.  **`BuildConfig`:** Gradle lisää avaimen automaattisesti generoituun `BuildConfig.java`-luokkaan staattiseksi vakioksi.
4.  **Käyttö koodissa:** Tämän jälkeen voimme viitata avaimeen turvallisesti koodissa, esimerkiksi `BuildConfig.OPEN_WEATHER_API_KEY`, ja antaa sen Retrofitille API-kutsua varten.