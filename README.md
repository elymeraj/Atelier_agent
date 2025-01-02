# Projet Multi-Agent 
## M1 Informatique - Université de Caen
### 2022 - 2023
#### PRONOST Sacha 21901956

## Description du projet
Ce projet à pour but d'implémenter à l'aide de la bibliothèque JADE, un système multi-agent permettant de simuler un atelier de production.

## Fichier de configuration
Le fichier de configuration est un fichier texte contenant les informations suivantes :
- Le temps de production d'un produit (qui fluctue en fonction du niveau de compétence des robots)
- La liste des compétences que peuvent avoir les robots
- La liste des produits que doit produire l'atelier

## Lancement du projet
## Étape 1 : Nettoyer les anciens fichiers compilés et créer les répertoires nécessaires
```bash
rm -rf build doc
mkdir build doc
```
## Étape 2 : Compiler toutes les classes Java
```bash

javac -cp ".:lib/jade.jar" -d build src/*.java
```
## Étape 3 : Générer la documentation Javadoc
```bash
javadoc -d doc -sourcepath src -classpath "lib/jade.jar" src/*.java
```
##Étape 4 : Exécuter le programme principal
Pour lancer le programme principal (Main) :
```bash
java -cp ".:lib/jade.jar:build" Main
```

# Conseils pour Nettoyer le Projet après Exécution

Après avoir exécuté et fini la consultation de votre projet, voici les étapes pour le nettoyer et le laisser propre. Cela inclut la suppression des fichiers générés automatiquement pour réduire l'encombrement.

---

# Nettoyer le Projet après Exécution

Voici les commandes à exécuter pour nettoyer votre projet et le laisser dans un état propre après consultation ou utilisation.

---

## Commandes de Nettoyage

1. **Supprimer les fichiers compilés et la documentation :**
Ces commandes permettent de supprimer les dossiers `build` et `doc`, qui contiennent respectivement les fichiers
`.class` générés lors de la compilation et la documentation générée par `javadoc`.
```bash
   rm -rf build doc
```
2. Supprimer les fichiers temporaires et autres fichiers générés par JADE
```bash
rm -f APDescription.txt MTPs-Main-Container.txt
```