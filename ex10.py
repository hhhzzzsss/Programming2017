year = 2018
yearsprinted = 0

while True:
    if year%4 == 0 and (year%400 == 0 or not year%100 == 0):
        print(str(year))
        yearsprinted += 1
    if yearsprinted == 20:
        break
    year += 1
