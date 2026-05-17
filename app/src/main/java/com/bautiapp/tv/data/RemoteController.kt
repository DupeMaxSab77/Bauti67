package com.bautiapp.tv.data

class RemoteController {
    private var currentIndex = 0
    private var selectedIndex = 0
    private var volumeLevel = 50
    
    fun navigateUp() {
        if (currentIndex > 0) {
            currentIndex--
            updateSelection()
        }
    }
    
    fun navigateDown() {
        currentIndex++
        updateSelection()
    }
    
    fun navigateLeft() {
        if (currentIndex > 0) {
            currentIndex--
            updateSelection()
        }
    }
    
    fun navigateRight() {
        currentIndex++
        updateSelection()
    }
    
    fun select(): Int {
        selectedIndex = currentIndex
        return selectedIndex
    }
    
    fun volumeUp() {
        if (volumeLevel < 100) {
            volumeLevel += 5
        }
    }
    
    fun volumeDown() {
        if (volumeLevel > 0) {
            volumeLevel -= 5
        }
    }
    
    fun getVolume(): Int = volumeLevel
    
    fun setVolume(level: Int) {
        volumeLevel = level.coerceIn(0, 100)
    }
    
    fun getCurrentIndex(): Int = currentIndex
    
    fun setMaxIndex(max: Int) {
        currentIndex = currentIndex.coerceIn(0, max - 1)
    }
    
    private fun updateSelection() {
        // Called whenever selection changes
    }
}
