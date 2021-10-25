//
//  Spinner.swift
//  TestSampleApp
//
//  Created by MiRan Kim on 2020/02/28.
//  Copyright © 2020 wini. All rights reserved.
//

import Foundation
import UIKit

//Vista de spinner común
final class Spinner {
    fileprivate static var activityIndicator: UIActivityIndicatorView?
    fileprivate static var style: UIActivityIndicatorView.Style = .whiteLarge
    fileprivate static var baseBackColor = UIColor(white: 0, alpha: 0.4)
    fileprivate static var baseColor = UIColor.white
    
    public static func start(from view: UIView, style: UIActivityIndicatorView.Style = Spinner.style, backgroundColor: UIColor = Spinner.baseBackColor, baseColor: UIColor = Spinner.baseColor) {
        
        guard Spinner.activityIndicator == nil else {
            return
        }
        
        let spinner = UIActivityIndicatorView(style: style)
        spinner.backgroundColor = backgroundColor
        spinner.color = baseColor
        spinner.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(spinner)
        
        addConstraints(to: view, with: spinner)
        
        Spinner.activityIndicator = spinner
        Spinner.activityIndicator?.startAnimating()
    }
    
    public static func stop() {
        Spinner.activityIndicator?.stopAnimating()
        Spinner.activityIndicator?.removeFromSuperview()
        Spinner.activityIndicator = nil
    }
    
    fileprivate static func addConstraints(to view:UIView, with spinner: UIActivityIndicatorView) {
        spinner.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        spinner.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        spinner.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        spinner.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
    }
}
