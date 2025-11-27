# Rogue_Hammer_2137K

## Opis

- Gra typu roguelike inspirowana The Binding of Isaac oraz Enter the Gungeon, stworzona w LibGDX jako projekt semestralny. Gracz przemierza losowo generowane poziomy, walczy z przeciwnikami, zbiera przedmioty i stara się przetrwać jak najdłużej.

## Funkcje

### Gracz

- Płynne poruszanie 2D (WASD, z normalizacją ruchu po przekątnej)
- Celowanie i strzelanie myszką, obrót postaci w kierunku celowania
- Animacje: poruszanie, otrzymywanie obrażeń, śmierć
- System zdrowia, obrażenia, śmierć i ekran Game Over
- Kolizje z przeciwnikami, obiektami i elementami mapy
- Ekwipunek broni i przedmiotów: podnoszenie, zmiana, używanie

### Broń i pociski

- Zróżnicowane statystki każdej broni: prędkość pocisku, obrażenia, szybkostrzelność, pojemność magazynka, semi/full-auto
- Dostępne bronie: pistolet, karabin, SMG, strzelba, snajperka
- Realistyczny system: spawn pocisków z lufy, trafienia, kolizje
- Magazynek oraz przeładowanie

### Przedmioty

- Typy: aktywne, pasywne, zużywalne
- Bonusy statystyk: szybkość ruchu, obrażenia, maksymalne HP itd.
- Przedmioty użytkowe: apteczki, skrzynki z amunicją
- Mikstury leczące, monety i klucze do skrzyń

### Przeciwnicy

- Różne typy przeciwników: goblin, ork, boss (różne statystyki i animacje)
- Sztuczna inteligencja: podążanie za graczem i atak
- System obrażeń i śmierci
- Losowa liczba i typ przeciwników w pokojach
- Szansa na drop przedmiotów po pokonaniu

### Świat i poziomy

- Losowe wybieranie układu poziomu z przygotowanych schematów
- System pokoi z generacją przeciwników i przedmiotów
- Drzwi odblokowujące się po wyczyszczeniu pokoju
- Elementy mapy: ściany, podświetlane drzwi
- Animowane elementy świata: pochodnie, paleniska, skrzynie, banery, pułapki
- Sklep z przedmiotami kupowanymi za monety
- Pokoje ze skrzyniami otwieranymi kluczami
- Możliwość przejścia do kolejnego poziomu przez pokój końcowy

### Interfejs użytkownika

- Przejrzyste UI
- Dynamiczny pasek zdrowia
- Lista przedmiotów i aktualnie wybrany przedmiot
- Własny celownik (crosshair)
- Minimapa pokazująca układ poziomu
- Skalowanie UI zależne od rozdzielczości

### System i ekrany gry

- Ekran menu, ustawień, główny, pauzy oraz końca gry
- Efekty dźwiękowe i muzyka (strzały, trafienia itp.)
- Ustawienia: głośność, rozdzielczość, przypisanie klawiszy
- Podsumowanie rozgrywki po śmierci
- Obsługa nowej gry, powrotu do menu, wyjścia

### Sterowanie

- Poruszanie: **W A S D**
- Celowanie: **myszka**
- Strzelanie: **lewy przycisk myszy**
- Interakcje: **E**
- Wybór broni: **scroll**
- Wybór przedmiotu: **1, 2**
- Użycie przedmiotu: **Q**

## Jak uruchomić (Windows)

## Jak uruchomić (Windows)

1. Otwórz projekt w IDE (VSCode) lub terminalu.
2. Uruchom z Gradle: `gradlew.bat desktop:run`
3. Upewnij się, że folder `assets` zawiera wymagane tekstury i dźwięki.

## Znane problemy

- Pociski: widoczne przesunięcia spawnów zależne od ruchu gracza
- Przeciwnicy: brak kolizji z pułapkami, rendering "pod nimi"
- Projekt miał być znacznie bogatszy, jednak ze względu na brak czasu obecna wersja jest ostateczna i okrojona z części funkcji

## Technikalia

- Język: Java (LibGDX)
- Platforma: desktop (Windows)

---
