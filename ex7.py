for i in range(1, 13):
    row = ""
    for j in range(1, 13):
        product = i*j
        digits = len(str(product))
        for k in range(4-digits):
            row += " "
        row += str(product)
    print(row)
