//
//  UserCell.swift
//  SQSMO_IOS
//
//  Created by wini on 2020/02/23.
//  Copyright © 2020 wini. All rights reserved.
//

import UIKit

//Celda de lista de autodiagnóstico la persona en autocuarentena
class UserCell: UITableViewCell {
    
    @IBOutlet var mView: UIView!
    @IBOutlet var dateItem: UILabel!

    override func layoutSubviews() {
        super.layoutSubviews()
        contentView.frame = contentView.frame.inset(by: UIEdgeInsets(top: 2, left: 0, bottom: 5, right: 0))
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        self.dateItem.text = nil
    }
}
