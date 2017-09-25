import random

number = random.randint(1, 100)
tries = 0;
prevguess = -1;

while True:
    stringguess = raw_input("guess: ")
    guess = int (stringguess)
    if not guess == prevguess:
        tries += 1
    prevguess = guess
    if guess < number:
        print("too low")
    elif guess > number:
        print("too high")
    else:
        print("correct")
        print("tries: " + str(tries))
        break
