# TinyPet par DEFOSSE Samuel et THIVET Simon

TinyPet est une application web réalisée dans le cadre du module Développement d'applications sur le CLOUD dans la formation M1 MIAGE.

Voici la liste des fonctionnalités présentes et fonctionnelles :
* Créer une petition en renseignant son titre ainsi que sa description via la rubrique "Créer une pétition"
* Afficher le top 100 des pétitions avec le plus de signatures. L'affichage montre le propriétaire de la pétition (adresse mail), le titre de la pétition, sa description, le nombre de signatures, ainsi qu'un bouton permettant de signer la pétition. Via la rubrique "Le top 100 des pétitions"
* Signer une pétition. Via le top 100 des pétitions on peut signer une pétiton via un bouton. Si la pétition n'a pas déjà été signée par l'utilisateur, un message lui indique qu'il vient de signer la pétition. Si la pétition a déjà été signée par l'utilisateur, un message lui indique qu'il a déjà signé celle-ci et elle n'est pas prise en compte. Il faut rafraîchir la page pour voir le nombre de signatures modifiés
* Afficher la liste des pétitions signées par un utilisateur. L'utilisateur connecté peut visualiser les pétitions qu'il a signés. Il y a le titre, la description et le nombre de signatures. Via la rubrique "Les pétitions signées
* Rechercher une pétition via un tag. Dans la barre de recherche, l'utilisateur peut renseigner un tag, et si des pétitions ont le tag correspondant, elles s'afficheront à l'écran. Pour simplifier la fonctionnalité, une pétition ne peut avoir qu'un tag, et le tag renseigné dans la barre de recherche doit être écrit de la même manière que celui de la ou les pétitions. 



Le schéma des données est le suivant :
![image](https://user-images.githubusercontent.com/43376319/122567873-717adf00-d049-11eb-917c-ea002ef8b392.png)
