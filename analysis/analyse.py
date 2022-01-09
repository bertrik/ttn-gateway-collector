#!/usr/bin/env python3

"""
   Analyses a CSV file of LoRaWAN gateway traffic.
"""

import argparse
import csv

def analyse(filename):
    """ Analyses a CSV file with the specified file name and prints the resuls to stdout """
    # the net ids of the major operators, others will be counted as 'Other'
    netids = {  '00': 'Experimental',
                '03': 'Proximus',
                '0A': 'KPN',
                '13': 'TheThingsNetwork',
                '24': 'Helium',
                '62': 'Operator_62'}

    # group airtime and total packets by operator
    timedict = {}
    pktsdict = {}
    time_total = 0
    pkts_total = 0
    with open(filename, encoding="utf-8") as csvfile:
        reader = csv.DictReader(csvfile, delimiter=',', quotechar='"')
        for row in reader:
            dev_addr = row['dev_addr']
            if dev_addr:
                netid = int(dev_addr, 16) >> 25
                netid_hex = f'{netid:0>2X}'
                operator = netids.get(netid_hex, 'Other')
                airtime = float(row['airtime'])
                timedict[operator] = timedict.get(operator, 0.0) + airtime
                pktsdict[operator] = pktsdict.get(operator, 0) + 1
                time_total += airtime
                pkts_total += 1

    print(f'Airtime by operator: ({time_total:.3f} total)')
    for operator,value in sorted(timedict.items(), key=lambda item: item[1], reverse=True):
        print(f'{operator:>20} : {value / time_total:>5.1%} = {value:.3f}')

    print(f'Packet # by operator: ({pkts_total} total)')
    for operator,value in sorted(pktsdict.items(), key=lambda item: item[1], reverse=True):
        print(f'{operator:>20} : {value / pkts_total:>5.1%} = {value}')

def main():
    """ The main entry point """
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--filename", help="The name of the CSV file to analyse",
                        default="gateway.csv")
    args = parser.parse_args()
    analyse(args.filename)

if __name__ == "__main__":
    main()
