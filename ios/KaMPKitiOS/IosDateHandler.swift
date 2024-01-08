//
//  IosDateHandler.swift
//  KaMPKitiOS
//
//  Created by Bartłomiej Pedryc on 05/01/2024.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import Foundation
import SwiftDate
import shared

class IosDateHandler: DateHandler {
    func getCurrentDate() -> String {
        return DateInRegion().toString()
    }
}
